package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;

import victors3136.ubb.mfpc.controller.requests.AddMappingRequest;
import victors3136.ubb.mfpc.controller.requests.AddWeaponRequest;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.controller.requests.AddCharacterRequest;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.repository.characters.CharacterRepository;
import victors3136.ubb.mfpc.repository.mappings.MappingRepository;
import victors3136.ubb.mfpc.repository.weapons.WeaponRepository;
import victors3136.ubb.mfpc.service.scheduling.model.Resource;
import victors3136.ubb.mfpc.service.scheduling.model.SqlOperation;
import victors3136.ubb.mfpc.service.scheduling.model.Transaction;
import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;
import victors3136.ubb.mfpc.service.scheduling.model.enums.Table;
import victors3136.ubb.mfpc.utils.ResultWithPossibleException;

import java.util.ArrayDeque;
import java.util.function.Supplier;

@Service
public class TransactionExecutorService {

    private final LockService lockService;
    private final CharacterRepository characterRepository;
    private final MappingRepository mappingRepository;
    private final WeaponRepository weaponRepository;

    public TransactionExecutorService(LockService lockService,
                                      CharacterRepository characterRepository, MappingRepository mappingRepository, WeaponRepository weaponRepository) {
        this.lockService = lockService;
        this.characterRepository = characterRepository;
        this.mappingRepository = mappingRepository;
        this.weaponRepository = weaponRepository;
    }

    private <T> ResultWithPossibleException<T> executeTransaction(Transaction transaction,
                                                                  Supplier<T> resultSupplier) {
        var undoStack = new ArrayDeque<SqlOperation>();
        try {
            for (var operation : transaction.getOperations()) {
                lockService.waitToLock(
                        transaction.getId(),
                        operation.resource(),
                        operation.lockType()
                );
                operation.doAction().run();
                Thread.sleep(1_000);
                undoStack.push(operation);
            }
            transaction.markCommited();
            return ResultWithPossibleException.success(resultSupplier.get());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            while (!undoStack.isEmpty()) {
                undoStack.pop().undoAction().run();
            }
            transaction.markAborted();
            return ResultWithPossibleException.failure(e);
        } finally {
            assert !transaction.isActive();
            lockService.releaseAllLocks(transaction.getId());
        }
    }

    public ResultWithPossibleException<Character> addCharacter(AddCharacterRequest req) {
        Transaction transaction = new Transaction();
        Character character = Character.fromAddRequest(req);
        transaction.addOperations(
                new SqlOperation(
                        new Resource(
                                Table.Characters,
                                String.valueOf(character.getId())
                        ),
                        LockType.Write,
                        () -> characterRepository.save(character),
                        () -> characterRepository.deleteById(character.getId())
                ));
        return executeTransaction(transaction, () -> character);
    }

    public ResultWithPossibleException<Weapon> addWeapon(AddWeaponRequest req) {
        Transaction transaction = new Transaction();
        Weapon weapon = Weapon.fromAddRequest(req);
        transaction.addOperations(
                new SqlOperation(
                        new Resource(
                                Table.Weapons,
                                String.valueOf(weapon.getId())
                        ),
                        LockType.Write,
                        () -> weaponRepository.save(weapon),
                        () -> weaponRepository.deleteById(weapon.getId())
                ));
        return executeTransaction(transaction, () -> weapon);
    }

    public ResultWithPossibleException<Mapping> addMapping(AddMappingRequest req) {
        Transaction transaction = new Transaction();
        var characterId = characterRepository.getIdByName(req.characterName())
                .orElseThrow();
        var weaponId = weaponRepository.getIdByName(req.weaponName())
                .orElseThrow();
        var mapping = new Mapping(characterId, weaponId);

        Resource characterIdResource = new Resource(
                Table.Characters,
                String.valueOf(characterId)
        ), weaponIdResource = new Resource(
                Table.Weapons,
                String.valueOf(weaponId)
        ), mappingEntryResource = new Resource(
                Table.Mappings,
                characterId + "-" + weaponId
        );

        transaction.addOperations(
                new SqlOperation(
                        characterIdResource,
                        LockType.Read,
                        () -> {
                        },
                        () -> {
                        }
                ), new SqlOperation(
                        weaponIdResource,
                        LockType.Read,
                        () -> {
                        },
                        () -> {
                        }
                ), new SqlOperation(
                        mappingEntryResource,
                        LockType.Write,
                        () -> mappingRepository.save(mapping),
                        () -> mappingRepository.delete(mapping)
                )
        );

        return executeTransaction(transaction, () -> mapping);
    }
}

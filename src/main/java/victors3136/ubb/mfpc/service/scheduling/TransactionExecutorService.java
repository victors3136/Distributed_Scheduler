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
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TransactionExecutorService {

    private final LockService lockService;
    private final CharacterRepository characterRepository;
    private final MappingRepository mappingRepository;
    private final WeaponRepository weaponRepository;
    public static final List<Transaction> Transactions = new Vector<>();

    public TransactionExecutorService(LockService lockService,
                                      CharacterRepository characterRepository, MappingRepository mappingRepository, WeaponRepository weaponRepository) {
        this.lockService = lockService;
        this.characterRepository = characterRepository;
        this.mappingRepository = mappingRepository;
        this.weaponRepository = weaponRepository;
    }

    private <T> ResultWithPossibleException<T> executeTransaction(Transaction transaction,
                                                                  Supplier<T> resultSupplier) {
        Transactions.add(transaction);
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
            while (!undoStack.isEmpty()) {
                undoStack.pop().undoAction().run();
            }
            transaction.markAborted();
            return ResultWithPossibleException.failure(e);
        } finally {
            assert !transaction.isActive();
            lockService.releaseAllLocks(transaction.getId());
            System.out.println(Transactions.stream().map(Objects::toString).collect(Collectors.joining("\n")));
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
        final Integer[] characterIdHolder = new Integer[1];
        final Integer[] weaponIdHolder = new Integer[1];
        final Mapping[] mappingHolder = new Mapping[1];

        transaction.addOperations(
                new SqlOperation(
                        new Resource(Table.Characters, req.characterName()),
                        LockType.Read,
                        () -> characterIdHolder[0] = characterRepository
                                .getIdByName(req.characterName())
                                .orElseThrow(),
                        () -> {
                        }
                )
        );
        transaction.addOperations(
                new SqlOperation(
                        new Resource(Table.Weapons, req.weaponName()),
                        LockType.Read,
                        () -> weaponIdHolder[0] = weaponRepository
                                .getIdByName(req.weaponName())
                                .orElseThrow(),
                        () -> {
                        }
                )
        );
        transaction.addOperations(
                new SqlOperation(
                        new Resource(
                                Table.Mappings,
                                req.characterName() + "-" + req.weaponName()
                        ),
                        LockType.Write,
                        () -> {
                            mappingHolder[0] = new Mapping(
                                    characterIdHolder[0],
                                    weaponIdHolder[0]
                            );
                            mappingRepository.save(mappingHolder[0]);
                        },
                        () -> {
                            if (mappingHolder[0] != null) {
                                mappingRepository.delete(mappingHolder[0]);
                            }
                        }
                )
        );

        return executeTransaction(transaction, () -> mappingHolder[0]);
    }
}

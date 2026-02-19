package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;

import victors3136.ubb.mfpc.controller.requests.AddMappingRequest;
import victors3136.ubb.mfpc.controller.requests.AddWeaponRequest;
import victors3136.ubb.mfpc.controller.requests.DropMappingRequest;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.controller.requests.AddCharacterRequest;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.repository.characters.CharacterRepository;
import victors3136.ubb.mfpc.repository.mappings.MappingRepository;
import victors3136.ubb.mfpc.repository.weapons.WeaponRepository;
import victors3136.ubb.mfpc.service.scheduling.model.Resource;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.Transaction;
import victors3136.ubb.mfpc.exceptions.ResultWithPossibleException;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.LockType.*;
import static victors3136.ubb.mfpc.service.scheduling.model.enums.Table.*;

@Service
public class TransactionExecutorService {
    private final LockService lockService;
    private final CharacterRepository characterRepository;
    private final MappingRepository mappingRepository;
    private final WeaponRepository weaponRepository;
    public final List<Transaction> Transactions = new Vector<>();
    private static final Runnable NoUndoActionRequired = () -> {
    };

    public TransactionExecutorService(LockService lockService,
                                      CharacterRepository characterRepository, MappingRepository mappingRepository, WeaponRepository weaponRepository) {
        this.lockService = lockService;
        this.characterRepository = characterRepository;
        this.mappingRepository = mappingRepository;
        this.weaponRepository = weaponRepository;
    }

    private <T> ResultWithPossibleException<T> executeTransaction(Transaction transaction, Supplier<T> result) {
        Transactions.add(transaction);
        var undoStack = new ArrayDeque<Operation>();
        try {
            for (var operation : transaction.getOperations()) {
                lockService.waitToLock(
                        transaction.getId(),
                        operation.resource(),
                        operation.lockType()
                );
                operation.doAction().run();
                Thread.sleep(1_500);
                undoStack.push(operation);
            }
            transaction.markCommited();
            return ResultWithPossibleException.success(result.get());
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
                new Operation(
                        new Resource(Characters, String.valueOf(character.getId())), Write,
                        () -> characterRepository.save(character),
                        () -> characterRepository.deleteById(character.getId())
                ));
        return executeTransaction(transaction, () -> character);
    }

    public ResultWithPossibleException<Weapon> addWeapon(AddWeaponRequest req) {
        Transaction transaction = new Transaction();
        Weapon weapon = Weapon.fromAddRequest(req);
        transaction.addOperations(
                new Operation(
                        new Resource(Weapons, String.valueOf(weapon.getId())), Write,
                        () -> weaponRepository.save(weapon),
                        () -> weaponRepository.deleteById(weapon.getId())
                ));
        return executeTransaction(transaction, () -> weapon);
    }

    public ResultWithPossibleException<Mapping> addMapping(AddMappingRequest req) {
        Transaction transaction = new Transaction();
        final AtomicInteger characterIdContainer = new AtomicInteger();
        final AtomicInteger weaponIdContainer = new AtomicInteger();
        final AtomicReference<Mapping> mappingContainer = new AtomicReference<>();

        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.characterName()), Read,
                        () -> characterIdContainer.set(
                                characterRepository
                                        .getIdByName(req.characterName())
                                        .orElseThrow()
                        ), NoUndoActionRequired),
                new Operation(
                        new Resource(Weapons, req.weaponName()), Read,
                        () -> weaponIdContainer.set(
                                weaponRepository
                                        .getIdByName(req.weaponName())
                                        .orElseThrow()
                        ), NoUndoActionRequired),
                new Operation(
                        new Resource(Mappings, req.characterName() + "-" + req.weaponName()), Write,
                        () -> {
                            mappingContainer.set(
                                    new Mapping(
                                            characterIdContainer.get(),
                                            weaponIdContainer.get()
                                    ));
                            mappingRepository.save(mappingContainer.get());
                        },
                        () -> {
                            if (mappingContainer.get() != null) {
                                mappingRepository.delete(mappingContainer.get());
                            }
                        }
                )
        );
        return executeTransaction(transaction, mappingContainer::get);
    }

    public ResultWithPossibleException<Mapping> dropMapping(DropMappingRequest req) {
        Transaction transaction = new Transaction();
        final AtomicInteger characterIdContainer = new AtomicInteger();
        final AtomicInteger weaponIdContainer = new AtomicInteger();
        final AtomicReference<Mapping> mappingContainer = new AtomicReference<>();
        transaction.addOperations(
                new Operation(new Resource(Weapons, req.weaponName()), Read,
                        () -> weaponIdContainer.set(
                                weaponRepository
                                        .getIdByName(req.weaponName())
                                        .orElseThrow()),
                        NoUndoActionRequired),
                new Operation(
                        new Resource(Characters, req.characterName()), Read,
                        () -> characterIdContainer.set(
                                characterRepository
                                        .getIdByName(req.characterName())
                                        .orElseThrow()),
                        NoUndoActionRequired),
                new Operation(
                        new Resource(Mappings, req.characterName() + "-" + req.weaponName()), Write,
                        () -> {
                            var mapping = new Mapping(
                                    characterIdContainer.get(),
                                    weaponIdContainer.get()
                            );
                            mappingContainer.set(mapping);
                            mappingRepository.delete(mapping);
                        },
                        () -> {
                            if (mappingContainer.get() != null) {
                                mappingRepository.save(mappingContainer.get());
                            }
                        }
                )
        );
        return executeTransaction(transaction, mappingContainer::get);
    }
}

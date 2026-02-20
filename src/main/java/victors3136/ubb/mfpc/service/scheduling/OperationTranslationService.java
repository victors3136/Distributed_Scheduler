package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;

import victors3136.ubb.mfpc.controller.requests.*;
import victors3136.ubb.mfpc.controller.responses.AttackMultipleSummary;
import victors3136.ubb.mfpc.controller.responses.AttackSummary;
import victors3136.ubb.mfpc.controller.responses.HealingSummary;
import victors3136.ubb.mfpc.controller.responses.NameDamageReceivedPair;
import victors3136.ubb.mfpc.exceptions.ValidationException;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.repository.characters.CharacterRepository;
import victors3136.ubb.mfpc.repository.mappings.MappingRepository;
import victors3136.ubb.mfpc.repository.weapons.WeaponRepository;
import victors3136.ubb.mfpc.service.scheduling.model.Resource;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.Transaction;
import victors3136.ubb.mfpc.exceptions.ResultWithPossibleException;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.LockType.*;
import static victors3136.ubb.mfpc.service.scheduling.model.enums.Table.*;

@Service
public class OperationTranslationService {
    private final TransactionExecutorService executorService;
    private final CharacterRepository characterRepository;
    private final MappingRepository mappingRepository;
    private final WeaponRepository weaponRepository;
    private static final Random RandomGenerator = new Random();
    private static final Runnable NoUndoActionRequired = () -> {
    };

    public OperationTranslationService(TransactionExecutorService executorService,
                                       CharacterRepository characterRepository,
                                       MappingRepository mappingRepository,
                                       WeaponRepository weaponRepository) {
        this.executorService = executorService;
        this.characterRepository = characterRepository;
        this.mappingRepository = mappingRepository;
        this.weaponRepository = weaponRepository;
    }

    public ResultWithPossibleException<Character> addCharacter(AddCharacterRequest req) {
        Transaction transaction = new Transaction();
        Character character = Character.fromAddRequest(req);
        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.displayName()),
                        Write,
                        () -> characterRepository.save(character),
                        () -> characterRepository.deleteById(character.getId())
                ));
        return executorService.submit(transaction, () -> character);
    }

    public ResultWithPossibleException<Weapon> addWeapon(AddWeaponRequest req) {
        Transaction transaction = new Transaction();
        Weapon weapon = Weapon.fromAddRequest(req);
        transaction.addOperations(
                new Operation(
                        new Resource(Weapons, req.displayName()),
                        Write,
                        () -> weaponRepository.save(weapon),
                        () -> weaponRepository.deleteById(weapon.getId())
                ));
        return executorService.submit(transaction, () -> weapon);
    }

    public ResultWithPossibleException<Mapping> addMapping(AddMappingRequest req) {
        Transaction transaction = new Transaction();
        final AtomicInteger characterIdContainer = new AtomicInteger();
        final AtomicInteger weaponIdContainer = new AtomicInteger();
        final AtomicReference<Mapping> mappingContainer = new AtomicReference<>();

        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.characterName()),
                        Read,
                        () -> characterIdContainer.set(characterRepository.getIdByName(req.characterName()).orElseThrow(
                                () -> new ValidationException("No character with name " + req.characterName() + " found."))
                        ), NoUndoActionRequired),
                new Operation(
                        new Resource(Weapons, req.weaponName()),
                        Read,
                        () -> weaponIdContainer.set(weaponRepository.getIdByName(req.weaponName()).orElseThrow(
                                () -> new ValidationException("No weapon with name " + req.weaponName() + " found."))
                        ), NoUndoActionRequired),
                new Operation(
                        new Resource(Mappings, req.characterName() + "-" + req.weaponName()),
                        Write,
                        () -> {
                            mappingContainer.set(new Mapping(characterIdContainer.get(), weaponIdContainer.get()));
                            mappingRepository.save(mappingContainer.get());
                        },
                        () -> {
                            if (mappingContainer.get() != null) {
                                mappingRepository.delete(mappingContainer.get());
                            }
                        }
                )
        );
        return executorService.submit(transaction, mappingContainer::get);
    }

    public ResultWithPossibleException<Mapping> dropMapping(DropMappingRequest req) {
        Transaction transaction = new Transaction();
        final AtomicInteger characterIdContainer = new AtomicInteger();
        final AtomicInteger weaponIdContainer = new AtomicInteger();
        final AtomicReference<Mapping> mappingContainer = new AtomicReference<>();
        transaction.addOperations(
                new Operation(new Resource(Weapons, req.weaponName()),
                        Read,
                        () -> weaponIdContainer.set(weaponRepository.getIdByName(req.weaponName()).orElseThrow(
                                () -> new ValidationException("No weapon with name " + req.weaponName() + " found."))
                        ),
                        NoUndoActionRequired),
                new Operation(
                        new Resource(Characters, req.characterName()),
                        Read,
                        () -> characterIdContainer.set(characterRepository.getIdByName(req.characterName()).orElseThrow(
                                () -> new ValidationException("No character with name " + req.characterName() + " found."))
                        ),
                        NoUndoActionRequired),
                new Operation(
                        new Resource(Mappings, req.characterName() + "-" + req.weaponName()),
                        Write,
                        () -> {
                            var mapping = new Mapping(characterIdContainer.get(), weaponIdContainer.get());
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
        return executorService.submit(transaction, mappingContainer::get);
    }

    public ResultWithPossibleException<Character> getCharacter(ReadCharacterStats req) {
        Transaction transaction = new Transaction();
        final AtomicReference<Character> characterHolder = new AtomicReference<>();

        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.characterName()),
                        Read,
                        () -> characterHolder.set(characterRepository.getByName(req.characterName()).orElseThrow(
                                () -> new ValidationException("No character with name " + req.characterName() + " found."))
                        ),
                        NoUndoActionRequired
                )
        );

        return executorService.submit(transaction, characterHolder::get);
    }

    public ResultWithPossibleException<Weapon> getWeapon(ReadWeaponStats req) {
        Transaction transaction = new Transaction();
        final AtomicReference<Weapon> weaponContainer = new AtomicReference<>();

        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.weaponName()),
                        Read,
                        () -> weaponContainer.set(weaponRepository.getByName(req.weaponName()).orElseThrow(
                                () -> new ValidationException("No weapon with name " + req.weaponName() + " found."))
                        ),
                        NoUndoActionRequired
                )
        );

        return executorService.submit(transaction, weaponContainer::get);
    }


    public ResultWithPossibleException<AttackSummary> attack(AttackRequest req) {
        Transaction transaction = new Transaction();
        final AtomicReference<Character> source = new AtomicReference<>();
        final AtomicReference<Character> target = new AtomicReference<>();
        final AtomicReference<Weapon> weapon = new AtomicReference<>();
        final AtomicInteger oldHp = new AtomicInteger();

        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.attackerName()),
                        Read,
                        () -> source.set(characterRepository.getByName(req.attackerName()).orElseThrow(
                                () -> new ValidationException("No character with name " + req.attackerName() + " found."))
                        ),
                        NoUndoActionRequired),
                new Operation(
                        new Resource(Weapons, req.weaponName()),
                        Read,
                        () -> weapon.set(weaponRepository.getByName(req.weaponName()).orElseThrow(
                                () -> new ValidationException("No weapon with name " + req.weaponName() + " found."))
                        ),
                        NoUndoActionRequired),
                new Operation(
                        new Resource(Mappings, req.attackerName() + "-" + req.weaponName()),
                        Read,
                        () -> mappingRepository.get(source.get().getId(), weapon.get().getId()).orElseThrow(
                                () -> new ValidationException(
                                        "No mapping between character with name " + req.attackerName()
                                                + " and weapon with name " + req.weaponName() + " found.")
                        ),
                        NoUndoActionRequired
                ),
                new Operation(
                        new Resource(Characters, req.targetName()),
                        Read,
                        () -> target.set(characterRepository.getByName(req.targetName()).orElseThrow(
                                () -> new ValidationException("No character with name " + req.targetName() + " found."))
                        ),
                        NoUndoActionRequired),
                new Operation(
                        new Resource(Characters, req.targetName()),
                        Write,
                        () -> {
                            var attacker = source.get();
                            var victim = target.get();
                            oldHp.set(victim.getHp());
                            int rawDamage = weapon.get().getDamage()
                                    + RandomGenerator.nextInt(attacker.getAttackModifier())
                                    - RandomGenerator.nextInt(victim.getDefenceModifier());
                            int damage = Math.max(0, rawDamage);
                            victim.setHp(victim.getHp() - damage);
                            characterRepository.save(victim);
                        },
                        () -> {
                            var victim = target.get();
                            victim.setHp(oldHp.get());
                            characterRepository.save(victim);
                        }
                )
        );

        return executorService.submit(transaction, () -> new AttackSummary(
                source.get().getName(),
                target.get().getName(),
                new NameDamageReceivedPair(weapon.get().getName(), oldHp.get() - target.get().getHp())
        ));
    }

    public ResultWithPossibleException<AttackMultipleSummary> attackMultiple(AttackMultipleRequest req) {
        Transaction transaction = new Transaction();
        final AtomicReference<Character> source = new AtomicReference<>();
        final AtomicReference<Weapon> weapon = new AtomicReference<>();
        final List<AttackSummary> individualAttacks = new Vector<>();

        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.attackerName()),
                        Read,
                        () -> source.set(characterRepository.getByName(req.attackerName()).orElseThrow(
                                () -> new ValidationException("No character with name " + req.attackerName() + " found."))
                        ), NoUndoActionRequired),
                new Operation(
                        new Resource(Weapons, req.weaponName()),
                        Read,
                        () -> weapon.set(weaponRepository.getByName(req.weaponName()).orElseThrow(
                                () -> new ValidationException("No weapon with name " + req.weaponName() + " found."))
                        ), NoUndoActionRequired),
                new Operation(
                        new Resource(Mappings, req.attackerName() + "-" + req.weaponName()),
                        Read,
                        () -> mappingRepository.get(source.get().getId(), weapon.get().getId()).orElseThrow(
                                () -> new ValidationException(
                                        "No mapping between character with name " + req.attackerName()
                                                + " and weapon with name " + req.weaponName() + " found.")
                        ), NoUndoActionRequired
                )
        );

        for (var targetName : req.targetNames()) {
            final AtomicReference<Character> targetContainer = new AtomicReference<>();
            final AtomicInteger oldHp = new AtomicInteger();

            transaction.addOperations(
                    new Operation(
                            new Resource(Characters, targetName),
                            Read,
                            () -> targetContainer.set(characterRepository.getByName(targetName).orElseThrow(
                                    () -> new ValidationException("No character with name " + req.targetNames() + " found."))
                            ), NoUndoActionRequired),
                    new Operation(
                            new Resource(Characters, targetName),
                            Write,
                            () -> {
                                var attacker = source.get();
                                var victim = targetContainer.get();
                                oldHp.set(victim.getHp());

                                int rawDamage = weapon.get().getDamage()
                                        + RandomGenerator.nextInt(attacker.getAttackModifier())
                                        - RandomGenerator.nextInt(victim.getDefenceModifier());
                                int damage = Math.max(0, rawDamage);

                                victim.setHp(Math.max(0, victim.getHp() - damage));
                                characterRepository.save(victim);

                                individualAttacks.add(
                                        new AttackSummary(
                                                attacker.getName(), weapon.get().getName(),
                                                new NameDamageReceivedPair(
                                                        victim.getName(),
                                                        damage
                                                )
                                        ));
                            },
                            () -> {
                                var victim = targetContainer.get();
                                if (victim != null) {
                                    victim.setHp(oldHp.get());
                                    characterRepository.save(victim);
                                }
                            }
                    )
            );
        }

        return executorService.submit(transaction, () -> new AttackMultipleSummary(
                        source.get().getName(),
                        weapon.get().getName(),
                        individualAttacks
                )
        );
    }

    public ResultWithPossibleException<HealingSummary> heal(HealCharacterRequest req) {
        Transaction transaction = new Transaction();
        final AtomicReference<Character> characterHolder = new AtomicReference<>();
        final AtomicInteger oldHp = new AtomicInteger();
        transaction.addOperations(
                new Operation(
                        new Resource(Characters, req.characterName()),
                        Read,
                        () -> {
                            final var target = characterRepository.getByName(req.characterName()).orElseThrow(
                                    () -> new ValidationException("No character with name " + req.characterName() + " found.")
                            );
                            characterHolder.set(target);
                            oldHp.set(target.getHp());
                        },
                        NoUndoActionRequired
                ),
                new Operation(
                        new Resource(Characters, req.characterName()),
                        Write,
                        () -> {
                            var newHp = oldHp.get() + req.hp();
                            characterRepository.updateHp(characterHolder.get().getId(), newHp);
                        },
                        () -> characterRepository.updateHp(characterHolder.get().getId(), oldHp.get())
                )
        );

        return executorService.submit(transaction, () -> new HealingSummary(req.characterName(), req.hp()));
    }
}



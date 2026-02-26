package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;

import victors3136.ubb.mfpc.controller.requests.*;
import victors3136.ubb.mfpc.controller.responses.AttackMultipleSummary;
import victors3136.ubb.mfpc.controller.responses.AttackSummary;
import victors3136.ubb.mfpc.controller.responses.HealingSummary;
import victors3136.ubb.mfpc.controller.responses.NameDamageReceivedPair;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.service.scheduling.model.Transaction;
import victors3136.ubb.mfpc.exceptions.Result;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
public class OperationTranslationService {
    private final TransactionExecutorService executorService;
    private final CharacterOperationService characterService;
    private final WeaponOperationService weaponService;
    private final MappingOperationService mappingService;

    private static final Random RandomGenerator = new Random();

    private static <T> Consumer<T> NoSideEffect() {
        return _ -> {
        };
    }


    public OperationTranslationService(TransactionExecutorService executorService,
                                       CharacterOperationService characterService,
                                       WeaponOperationService weaponService,
                                       MappingOperationService mappingService) {
        this.executorService = executorService;
        this.characterService = characterService;
        this.weaponService = weaponService;
        this.mappingService = mappingService;
    }

    public Result<Character> addCharacter(AddCharacterRequest req) {
        final var transaction = new Transaction();
        final var character = Character.fromAddRequest(req);
        transaction.enqueue(characterService.insert(() -> character, NoSideEffect()));
        return executorService.submit(transaction, () -> character);
    }

    public Result<Weapon> addWeapon(AddWeaponRequest req) {
        final var transaction = new Transaction();
        final var weapon = Weapon.fromAddRequest(req);
        transaction.enqueue(weaponService.insert(() -> weapon, NoSideEffect()));
        return executorService.submit(transaction, () -> weapon);
    }

    public Result<Mapping> addMapping(AddMappingRequest req) {
        final var transaction = new Transaction();
        final var characterResult = new AtomicReference<Character>();
        final var weaponResult = new AtomicReference<Weapon>();
        final var mappingResult = new AtomicReference<Mapping>();

        transaction.enqueue(
                characterService.select(req::characterName, characterResult::set),
                weaponService.select(req::weaponName, weaponResult::set),
                mappingService.insert(() -> new Mapping(characterResult.get().getId(), weaponResult.get().getId()), mappingResult::set)
        );
        return executorService.submit(transaction, mappingResult::get);
    }

    public Result<Mapping> dropMapping(DropMappingRequest req) {
        final var transaction = new Transaction();
        final var characterResult = new AtomicReference<Character>();
        final var weaponResult = new AtomicReference<Weapon>();
        final var mappingContainer = new AtomicReference<Mapping>();
        transaction.enqueue(
                weaponService.select(req::weaponName, weaponResult::set),
                characterService.select(req::characterName, characterResult::set),
                mappingService.delete(() -> {
                    var mappingRef = new Mapping(
                            characterResult.get().getId(),
                            weaponResult.get().getId()
                    );
                    mappingContainer.set(mappingRef);
                    return mappingRef;
                }, NoSideEffect())
        );
        return executorService.submit(transaction, mappingContainer::get);
    }

    public Result<Character> getCharacter(ReadCharacterStats req) {
        final var transaction = new Transaction();
        final var characterResult = new AtomicReference<Character>();

        transaction.enqueue(characterService.select(req::characterName, characterResult::set));

        return executorService.submit(transaction, characterResult::get);
    }

    public Result<Weapon> getWeapon(ReadWeaponStats req) {
        final var transaction = new Transaction();
        final var weaponResult = new AtomicReference<Weapon>();
        transaction.enqueue(weaponService.select(req::weaponName, weaponResult::set));
        return executorService.submit(transaction, weaponResult::get);
    }


    public Result<AttackSummary> attack(AttackRequest req) {
        final var transaction = new Transaction();
        final var attacker = new AtomicReference<Character>();
        final var weapon = new AtomicReference<Weapon>();
        final var originalCharacter = new AtomicReference<Character>();
        final var updatedCharacter = new AtomicReference<Character>();
        final var newHp = new AtomicInteger();

        transaction.enqueue(
                characterService.select(req::attackerName, attacker::set),
                weaponService.select(req::weaponName, weapon::set),
                mappingService.select(() -> new CharacterWeaponId(attacker.get().getId(), weapon.get().getId()), NoSideEffect()),
                characterService.select(req::targetName, target -> {
                    originalCharacter.set(target);
                    int rawDamage = weapon.get().getDamage()
                            + RandomGenerator.nextInt(attacker.get().getAttackModifier())
                            - RandomGenerator.nextInt(target.getDefenceModifier());
                    int damage = Math.max(0, rawDamage);
                    int rawNewHp = target.getHp() - damage;
                    newHp.set(Math.max(0, rawNewHp));
                }),
                characterService.update(() -> originalCharacter.get().copyWithHp(newHp.get()), originalCharacter::get, updatedCharacter::set)
        );

        return executorService.submit(transaction, () -> new AttackSummary(
                attacker.get().getName(),
                originalCharacter.get().getName(),
                new NameDamageReceivedPair(weapon.get().getName(), originalCharacter.get().getHp() - newHp.get())
        ));
    }

    public Result<AttackMultipleSummary> attackMultiple(AttackMultipleRequest req) {
        final var transaction = new Transaction();
        final var attacker = new AtomicReference<Character>();
        final var weapon = new AtomicReference<Weapon>();
        final var individualAttacks = new Vector<AttackSummary>();

        transaction.enqueue(
                characterService.select(req::attackerName, attacker::set),
                weaponService.select(req::weaponName, weapon::set),
                mappingService.select(() -> new CharacterWeaponId(attacker.get().getId(), weapon.get().getId()), NoSideEffect())
        );

        for (var targetName : req.targetNames()) {
            final var originalCharacter = new AtomicReference<Character>();
            final var newHp = new AtomicInteger();

            transaction.enqueue(
                    characterService.select(() -> targetName, targetRef -> {
                        originalCharacter.set(targetRef);
                        int rawDamage = weapon.get().getDamage()
                                + RandomGenerator.nextInt(attacker.get().getAttackModifier())
                                - RandomGenerator.nextInt(targetRef.getDefenceModifier());
                        int damage = Math.max(0, rawDamage);
                        int rawNewHp = targetRef.getHp() - damage;
                        newHp.set(Math.max(0, rawNewHp));
                    }),
                    characterService.update(() -> originalCharacter.get().copyWithHp(newHp.get()), originalCharacter::get, victim -> {
                        individualAttacks.add(
                                new AttackSummary(
                                        attacker.get().getName(), weapon.get().getName(),
                                        new NameDamageReceivedPair(
                                                victim.getName(),
                                                originalCharacter.get().getHp() - newHp.get()
                                        )
                                ));
                    })
            );
        }

        return executorService.submit(transaction, () -> new AttackMultipleSummary(
                        attacker.get().getName(),
                        weapon.get().getName(),
                        individualAttacks
                )
        );
    }

    public Result<HealingSummary> heal(HealCharacterRequest req) {
        Transaction transaction = new Transaction();
        final AtomicReference<Character> originalCharacter = new AtomicReference<>();
        transaction.enqueue(
                characterService.select(req::characterName, originalCharacter::set),
                characterService.update(
                        () -> originalCharacter.get().copyWithHp(originalCharacter.get().getHp() + req.hp()),
                        originalCharacter::get,
                        NoSideEffect()
                )
        );

        return executorService.submit(transaction, () -> new HealingSummary(req.characterName(), req.hp()));
    }
}



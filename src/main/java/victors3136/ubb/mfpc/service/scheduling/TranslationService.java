package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.controller.requests.*;
import victors3136.ubb.mfpc.controller.responses.AttackMultipleSummary;
import victors3136.ubb.mfpc.controller.responses.AttackSummary;
import victors3136.ubb.mfpc.controller.responses.HealingSummary;
import victors3136.ubb.mfpc.controller.responses.NameDamageReceivedPair;
import victors3136.ubb.mfpc.exceptions.Result;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.service.scheduling.transactions.TransactionExecutorService;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class TranslationService {
    private final TransactionExecutorService executorService;

    private static final Random RandomGenerator = new Random();

    public TranslationService(TransactionExecutorService executorService) {
        this.executorService = executorService;
    }

    public Result<Character> getCharacter(ReadCharacterStats req) {
        var transactionBuilder = executorService.newTransaction();
        var character = transactionBuilder.select(Character.class, req::characterName);
        return executorService.submit(transactionBuilder, character.asSupplier());
    }

    public Result<Weapon> getWeapon(ReadWeaponStats req) {
        var transactionBuilder = executorService.newTransaction();
        var weapon = transactionBuilder.select(Weapon.class, req::weaponName);
        return executorService.submit(transactionBuilder, weapon.asSupplier());
    }

    public Result<Character> addCharacter(AddCharacterRequest req) {
        var transactionBuilder = executorService.newTransaction();
        var character = transactionBuilder.insert(Character.class, () -> Character.fromAddRequest(req));
        return executorService.submit(transactionBuilder, character.asSupplier());
    }

    public Result<Weapon> addWeapon(AddWeaponRequest req) {
        var transactionBuilder = executorService.newTransaction();
        var character = transactionBuilder.insert(Weapon.class, () -> Weapon.fromAddRequest(req));
        return executorService.submit(transactionBuilder, character.asSupplier());
    }

    public Result<Mapping> addMapping(AddMappingRequest req) {
        var transactionBuilder = executorService.newTransaction();
        var character = transactionBuilder.select(Character.class, req::characterName);
        var weapon = transactionBuilder.select(Weapon.class, req::weaponName);
        var mapping = transactionBuilder.insert(Mapping.class,
                () -> Mapping.fromRequest(character.get().getId(), weapon.get().getId()));
        return executorService.submit(transactionBuilder, mapping.asSupplier());
    }

    public Result<Mapping> dropMapping(DropMappingRequest req) {
        var transactionBuilder = executorService.newTransaction();
        var character = transactionBuilder.select(Character.class, req::characterName);
        var weapon = transactionBuilder.select(Weapon.class, req::weaponName);
        var mapping = transactionBuilder.delete(Mapping.class,
                () -> Mapping.fromRequest(character.get().getId(), weapon.get().getId()));
        return executorService.submit(transactionBuilder, mapping.asSupplier());
    }

    public Result<AttackSummary> attack(AttackRequest req) {
        var transactionBuilder = executorService.newTransaction();
        var attacker = transactionBuilder.select(Character.class, req::attackerName);
        var weapon = transactionBuilder.select(Weapon.class, req::weaponName);
        transactionBuilder.select(Mapping.class,
                () -> new CharacterWeaponId(attacker.get().getId(), weapon.get().getId()));
        var newHp = new AtomicInteger();
        var target = transactionBuilder.select(Character.class, req::targetName, t ->
                newHp.set(computeNewHp(t, attacker.get(), weapon.get())));
        var _ = transactionBuilder.update(Character.class,
                () -> target.get().copyWithHp(newHp.get()),
                target.asSupplier());

        return executorService.submit(transactionBuilder, () -> new AttackSummary(
                attacker.get().getName(),
                target.get().getName(),
                new NameDamageReceivedPair(weapon.get().getName(),
                        target.get().getHp() - newHp.get())));
    }

    public Result<AttackMultipleSummary> attackMultiple(AttackMultipleRequest req) {
        var transactionBuilder = executorService.newTransaction();
        var attacker = transactionBuilder.select(Character.class, req::attackerName);
        var weapon = transactionBuilder.select(Weapon.class, req::weaponName);
        transactionBuilder.select(Mapping.class,
                () -> new CharacterWeaponId(attacker.get().getId(), weapon.get().getId()));
        var individualAttacks = new Vector<AttackSummary>();

        for (var targetName : req.targetNames()) {
            var newHp = new AtomicInteger();
            var target = transactionBuilder.select(
                    Character.class,
                    () -> targetName,
                    victim -> newHp.set(computeNewHp(victim, attacker.get(), weapon.get())));
            var _ = transactionBuilder.update(
                    Character.class,
                    () -> target.get().copyWithHp(newHp.get()),
                    target.asSupplier(),
                    victim -> individualAttacks.add(
                            new AttackSummary(
                                    attacker.get().getName(),
                                    weapon.get().getName(),
                                    new NameDamageReceivedPair(
                                            victim.getName(),
                                            target.get().getHp() - newHp.get()))));
        }

        return executorService.submit(transactionBuilder, () -> new AttackMultipleSummary(
                attacker.get().getName(), weapon.get().getName(), individualAttacks));
    }

    public Result<HealingSummary> heal(HealCharacterRequest req) {
        var transactionBuilder = executorService.newTransaction();
        var original = transactionBuilder.select(Character.class, req::characterName);
        transactionBuilder.update(Character.class,
                () -> original.get().copyWithHp(original.get().getHp() + req.hp()),
                original.asSupplier());
        return executorService.submit(transactionBuilder, () -> new HealingSummary(req.characterName(), req.hp()));
    }

    private int computeNewHp(Character target, Character attacker, Weapon weapon) {
        var rawDamage = weapon.getDamage()
                + RandomGenerator.nextInt(attacker.getAttackModifier())
                - RandomGenerator.nextInt(target.getDefenceModifier());
        var damage = Math.max(0, rawDamage);
        return Math.max(0, target.getHp() - damage);
    }
}
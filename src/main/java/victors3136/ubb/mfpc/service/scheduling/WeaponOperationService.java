package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.exceptions.ValidationException;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.repository.weapons.WeaponRepository;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;
import victors3136.ubb.mfpc.service.scheduling.model.resources.ResolvedResource;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static victors3136.ubb.mfpc.service.scheduling.OperationCreationService.*;

@Service
public class WeaponOperationService implements OperationService<String, Weapon> {
    private final WeaponRepository weaponRepository;

    @Autowired
    public WeaponOperationService(WeaponRepository weaponRepository) {
        this.weaponRepository = weaponRepository;
    }

    public Operation select(Supplier<String> weaponName, Consumer<Weapon> sideEffect) {
        return selectOperation(
                FutureResource.ofWeapon(weaponName),
                () -> weaponRepository.getByName(weaponName.get()).orElseThrow(() -> new ValidationException(
                        "No Weapon named %s found.".formatted(weaponName))
                ),
                sideEffect,
                "weapon"
        );
    }

    public Operation insert(Supplier<Weapon> weapon, Consumer<Weapon> sideEffect) {
        return insertOperation(
                FutureResource.ofWeapon(() -> weapon.get().getName()),
                weapon,
                weaponRef -> sideEffect.accept(weaponRepository.save(weaponRef)),
                weaponRepository::delete,
                "weapon"
        );
    }

    public Operation delete(Supplier<Weapon> weapon, Consumer<Weapon> sideEffect) {
        return deleteOperation(
                FutureResource.ofWeapon(() -> weapon.get().getName()),
                weapon,
                weaponRef -> {
                    weaponRepository.delete(weaponRef);
                    sideEffect.accept(weaponRef);
                },
                weaponRepository::save,
                "weapon"
        );
    }

    public Operation update(Supplier<Weapon> newWeapon, Supplier<Weapon> oldWeapon, Consumer<Weapon> sideEffect) {
        return updateOperation(
                FutureResource.ofWeapon(() -> newWeapon.get().getName()),
                newWeapon,
                oldWeapon,
                wpn -> sideEffect.accept(weaponRepository.save(wpn)),
                weaponRepository::save,
                "weapon"
        );
    }
}

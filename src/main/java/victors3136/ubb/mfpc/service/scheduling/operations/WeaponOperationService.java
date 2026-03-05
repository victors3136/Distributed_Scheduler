package victors3136.ubb.mfpc.service.scheduling.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.repository.weapons.WeaponRepository;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

import java.util.Optional;
import java.util.function.Supplier;

@Service
public class WeaponOperationService
        extends AbstractOperationService<String, Weapon, WeaponRepository> {

    @Autowired
    public WeaponOperationService(WeaponRepository repo) {
        super(repo, FutureResource::ofWeapon, "weapon");
    }

    @Override
    protected Optional<Weapon> getByKey(Supplier<String> key) {
        return repository.getByLockKey(key.get());
    }
}
package victors3136.ubb.mfpc.service.scheduling.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.repository.mappings.MappingRepository;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

import java.util.Optional;
import java.util.function.Supplier;

@Service
public class MappingOperationService
        extends AbstractOperationService<CharacterWeaponId, Mapping, MappingRepository> {

    @Autowired
    public MappingOperationService(MappingRepository repo) {
        super(repo, FutureResource::ofMapping, "mapping");
    }

    @Override
    protected Optional<Mapping> getByKey(Supplier<CharacterWeaponId> key) {
        return repository.getByLockKey(
                key.get().getCharacterId(),
                key.get().getWeaponId()
        );
    }
}
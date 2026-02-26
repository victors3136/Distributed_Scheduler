package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.exceptions.ValidationException;
import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.repository.mappings.MappingRepository;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static victors3136.ubb.mfpc.service.scheduling.OperationCreationService.*;

@Service
public class MappingOperationService implements OperationService<CharacterWeaponId, Mapping> {
    private final MappingRepository mappingRepository;

    @Autowired
    public MappingOperationService(MappingRepository mappingRepository) {
        this.mappingRepository = mappingRepository;
    }

    @Override
    public Operation select(Supplier<CharacterWeaponId> key, Consumer<Mapping> sideEffect) {
        return selectOperation(
                FutureResource.ofMapping(key),
                () -> mappingRepository.get(key.get().getCharacterId(), key.get().getWeaponId()).orElseThrow(() -> new ValidationException(
                        "No mapping between character %d and weapon %d found.".formatted(key.get().getCharacterId(), key.get().getWeaponId()))
                ),
                sideEffect
        );
    }

    @Override
    public Operation insert(Supplier<Mapping> mapping, Consumer<Mapping> sideEffect) {
        return insertOperation(
                FutureResource.ofMapping(() -> mapping.get().getId()),
                mapping,
                mappingRef -> sideEffect.accept(mappingRepository.save(mappingRef)),
                mappingRepository::delete

        );
    }

    @Override
    public Operation delete(Supplier<Mapping> mapping, Consumer<Mapping> sideEffect) {
        return deleteOperation(
                FutureResource.ofMapping(() -> mapping.get().getId()),
                mapping,
                mappingRef -> {
                    mappingRepository.delete(mappingRef);
                    sideEffect.accept(mappingRef);
                },
                mappingRepository::save
        );
    }

    @Override
    public Operation update(Supplier<Mapping> newMapping, Supplier<Mapping> oldMapping, Consumer<Mapping> sideEffect) {
        return updateOperation(
                FutureResource.ofMapping(() -> newMapping.get().getId()),
                newMapping,
                oldMapping,
                mappingRef -> sideEffect.accept(mappingRepository.save(mappingRef)),
                mappingRepository::save
        );
    }
}

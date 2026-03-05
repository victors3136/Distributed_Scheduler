package victors3136.ubb.mfpc.service.scheduling.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import victors3136.ubb.mfpc.exceptions.ValidationException;
import victors3136.ubb.mfpc.model.HasKey;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static victors3136.ubb.mfpc.service.scheduling.operations.OperationCreationService.*;

public abstract class AbstractOperationService<
        KeyType,
        EntityType extends HasKey<KeyType>,
        Repository extends JpaRepository<EntityType, ?>>
        implements OperationService<KeyType, EntityType> {

    protected final Repository repository;
    private final Function<Supplier<KeyType>, FutureResource> futureResourceFactory;
    private final String entityLabel;

    protected AbstractOperationService(
            Repository repository,
            Function<Supplier<KeyType>, FutureResource> futureResourceFactory,
            String entityLabel) {
        this.repository = repository;
        this.futureResourceFactory = futureResourceFactory;
        this.entityLabel = entityLabel;
    }

    @Override
    public Operation select(Supplier<KeyType> key, Consumer<EntityType> sideEffect) {
        return selectOperation(
                futureResourceFactory.apply(key),
                () -> getByKey(key).orElseThrow(() ->
                        new ValidationException("No %s with key %s found."
                                .formatted(entityLabel, key.get()))),
                sideEffect,
                entityLabel
        );
    }

    @Override
    public Operation insert(Supplier<EntityType> entry, Consumer<EntityType> sideEffect) {
        return insertOperation(
                futureResourceFactory.apply(() -> entry.get().getKey()),
                entry,
                ref -> sideEffect.accept(repository.save(ref)),
                repository::delete,
                entityLabel
        );
    }

    @Override
    public Operation delete(Supplier<EntityType> entry, Consumer<EntityType> sideEffect) {
        return deleteOperation(
                futureResourceFactory.apply(() -> entry.get().getKey()),
                entry,
                ref -> {
                    repository.delete(ref);
                    sideEffect.accept(ref);
                },
                repository::save,
                entityLabel
        );
    }

    @Override
    public Operation update(Supplier<EntityType> newEntry, Supplier<EntityType> oldEntry, Consumer<EntityType> sideEffect) {
        return updateOperation(
                futureResourceFactory.apply(() -> newEntry.get().getKey()),
                newEntry,
                oldEntry,
                ref -> sideEffect.accept(repository.save(ref)),
                repository::save,
                entityLabel
        );
    }

    protected abstract Optional<EntityType> getByKey(Supplier<KeyType> key);
}

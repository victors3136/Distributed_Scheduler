package victors3136.ubb.mfpc.service.scheduling.operations;

import victors3136.ubb.mfpc.model.HasKey;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface OperationService<KeyType, EntityType extends HasKey<KeyType>> {
    Operation select(Supplier<KeyType> keySupplier, Consumer<EntityType> sideEffect);

    Operation insert(Supplier<EntityType> entrySupplier, Consumer<EntityType> sideEffect);

    Operation delete(Supplier<EntityType> entrySupplier, Consumer<EntityType> sideEffect);

    Operation update(Supplier<EntityType> newEntry, Supplier<EntityType> oldEntry, Consumer<EntityType> sideEffect);
}

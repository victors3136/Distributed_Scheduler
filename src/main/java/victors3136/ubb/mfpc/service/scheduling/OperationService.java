package victors3136.ubb.mfpc.service.scheduling;

import victors3136.ubb.mfpc.model.HasKey;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface OperationService<K, T extends HasKey<K>> {
    Operation select(Supplier<K> keySupplier, Consumer<T> sideEffect);

    Operation insert(Supplier<T> entrySupplier, Consumer<T> sideEffect);

    Operation delete(Supplier<T> entrySupplier, Consumer<T> sideEffect);

    Operation update(Supplier<T> newEntry, Supplier<T> oldEntry, Consumer<T> sideEffect);
}

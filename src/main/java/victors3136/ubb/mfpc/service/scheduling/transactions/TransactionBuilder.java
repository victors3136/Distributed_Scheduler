package victors3136.ubb.mfpc.service.scheduling.transactions;

import victors3136.ubb.mfpc.model.HasKey;
import victors3136.ubb.mfpc.service.scheduling.operations.OperationServiceRegistry;
import victors3136.ubb.mfpc.service.scheduling.model.Transaction;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TransactionBuilder {
    private final Transaction transaction;
    private final OperationServiceRegistry registry;

    public TransactionBuilder(Transaction transaction, OperationServiceRegistry registry) {
        this.transaction = transaction;
        this.registry = registry;
    }

    public <KeyType, EntityType extends HasKey<KeyType>> Reference<EntityType> select(Class<EntityType> entity,
                                                                                      Supplier<KeyType> key) {
        var reference = new Reference<EntityType>();
        transaction.enqueue(registry.get(entity).select(key, reference::set));
        return reference;
    }

    public <KeyType, EntityType extends HasKey<KeyType>> Reference<EntityType> insert(Class<EntityType> entity,
                                                                                      Supplier<EntityType> value) {
        var reference = new Reference<EntityType>();
        transaction.enqueue(registry.get(entity).insert(value, reference::set));
        return reference;
    }

    public <KeyType, EntityType extends HasKey<KeyType>> Reference<EntityType> delete(Class<EntityType> entity,
                                                                                      Supplier<EntityType> value) {
        var reference = new Reference<EntityType>();
        transaction.enqueue(registry.get(entity).delete(value, reference::set));
        return reference;
    }

    public <KeyType, EntityType extends HasKey<KeyType>> Reference<EntityType> update(Class<EntityType> entity,
                                                                                      Supplier<EntityType> next,
                                                                                      Supplier<EntityType> previous) {
        var reference = new Reference<EntityType>();
        transaction.enqueue(registry.get(entity).update(next, previous, reference::set));
        return reference;
    }

    public <KeyType, EntityType extends HasKey<KeyType>> Reference<EntityType> select(Class<EntityType> entity,
                                                                                      Supplier<KeyType> key,
                                                                                      Consumer<EntityType> sideEffect) {
        var reference = new Reference<EntityType>();
        transaction.enqueue(registry.get(entity).select(key, found -> {
            reference.set(found);
            sideEffect.accept(found);
        }));
        return reference;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <KeyType, EntityType extends HasKey<KeyType>> Reference<EntityType> update(Class<EntityType> entity,
                                                                                      Supplier<EntityType> next,
                                                                                      Supplier<EntityType> prev,
                                                                                      Consumer<EntityType> sideEffect) {
        var reference = new Reference<EntityType>();
        transaction.enqueue(registry.get(entity).update(next, prev, found -> {
            reference.set(found);
            sideEffect.accept(found);
        }));
        return reference;
    }

    public Transaction build() {
        return transaction;
    }
}

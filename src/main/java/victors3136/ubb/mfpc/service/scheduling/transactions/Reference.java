package victors3136.ubb.mfpc.service.scheduling.transactions;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Reference<EntityType> {
    private final AtomicReference<EntityType> data = new AtomicReference<>();

    void set(EntityType value) {
        data.set(value);
    }

    public EntityType get() {
        return data.get();
    }

    public Supplier<EntityType> asSupplier() {
        return data::get;
    }

}

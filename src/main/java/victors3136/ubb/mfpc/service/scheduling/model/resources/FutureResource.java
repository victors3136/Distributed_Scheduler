package victors3136.ubb.mfpc.service.scheduling.model.resources;

import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;
import victors3136.ubb.mfpc.service.scheduling.model.enums.Table;

import java.util.function.Supplier;

public record FutureResource(Supplier<Table> tableSupplier,
                             Supplier<String> resourceIdSupplier) implements Resource {
    public FutureResource(Supplier<ResolvedResource> supplier) {
        this(() -> supplier.get().table(), () -> supplier.get().resourceId());
    }

    public static FutureResource ofCharacter(Supplier<String> name) {
        return new FutureResource(() -> ResolvedResource.ofCharacter(name.get()));
    }

    public static FutureResource ofWeapon(Supplier<String> name) {
        return new FutureResource(() -> ResolvedResource.ofWeapon(name.get()));
    }

    public static FutureResource ofMapping(Supplier<CharacterWeaponId> id) {
        return new FutureResource(() -> ResolvedResource.ofMapping(id.get()));
    }

    @Override
    public Table table() {
        return tableSupplier.get();
    }

    @Override
    public String resourceId() {
        return resourceIdSupplier.get();
    }

    @Override
    public ResolvedResource get() {
        return new ResolvedResource(table(), resourceId());
    }
}

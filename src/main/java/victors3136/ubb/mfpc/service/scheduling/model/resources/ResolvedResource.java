package victors3136.ubb.mfpc.service.scheduling.model.resources;

import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;
import victors3136.ubb.mfpc.service.scheduling.model.enums.Table;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.Table.*;

public record ResolvedResource(Table table, String resourceId) implements Resource {
    public static ResolvedResource ofCharacter(String name) {
        return new ResolvedResource(Characters, name);
    }

    public static ResolvedResource ofWeapon(String name) {
        return new ResolvedResource(Weapons, name);
    }

    public static ResolvedResource ofMapping(CharacterWeaponId id) {
        return new ResolvedResource(Mappings, "%d:%d".formatted(id.getCharacterId(), id.getWeaponId()));
    }

    public ResolvedResource get() {
        return this;
    }
}


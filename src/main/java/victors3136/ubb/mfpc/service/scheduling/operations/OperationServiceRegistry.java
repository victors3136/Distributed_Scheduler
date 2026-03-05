package victors3136.ubb.mfpc.service.scheduling.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import victors3136.ubb.mfpc.model.HasKey;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;

import java.util.Map;

@Component
public class OperationServiceRegistry {
    private final Map<Class<?>, OperationService<?, ?>> services;

    @Autowired
    public OperationServiceRegistry(
            CharacterOperationService characterService,
            WeaponOperationService weaponService,
            MappingOperationService mappingService) {
        this.services = Map.of(
                Character.class, characterService,
                Weapon.class, weaponService,
                Mapping.class, mappingService
        );
    }

    @SuppressWarnings("unchecked")
    public <KeyType, EntityType extends HasKey<KeyType>> OperationService<KeyType, EntityType> get(Class<EntityType> entityClass) {
        var service = services.get(entityClass);
        if (service == null) throw new IllegalArgumentException("No service for " + entityClass);
        return (OperationService<KeyType, EntityType>) service;
    }
}

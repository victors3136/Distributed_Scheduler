package victors3136.ubb.mfpc.model.mappings;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import victors3136.ubb.mfpc.model.HasKey;

@Getter
@Entity
@Table(name = "mappings")
public class Mapping implements HasKey<CharacterWeaponId> {

    @EmbeddedId
    private CharacterWeaponId id;

    public Mapping() {
    }

    public Mapping(Integer characterId, Integer weaponId) {
        this.id = new CharacterWeaponId(characterId, weaponId);
    }

    public static Mapping fromRequest(Integer characterId, Integer weaponId) {
        return new Mapping(characterId, weaponId);
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "id=" + id +
                '}';
    }

    @Override
    public CharacterWeaponId getKey() {
        return id;
    }
}
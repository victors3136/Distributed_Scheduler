package victors3136.ubb.mfpc.model.mappings;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "mappings")
public class Mapping {

    @EmbeddedId
    private CharacterWeaponId id;

    public Mapping() {}

    public Mapping(Integer characterId, Integer weaponId) {
        this.id = new CharacterWeaponId(characterId, weaponId);
    }

    public Mapping setId(CharacterWeaponId id) {
        this.id = id;
        return this;
    }

    public int getCharacterId() {
        return id != null ? id.getCharacterId() : 0;
    }

    public int getWeaponId() {
        return id != null ? id.getWeaponId() : 0;
    }

    public Mapping setCharacterId(int characterId) {
        if (id == null) id = new CharacterWeaponId();
        id.setCharacterId(characterId);
        return this;
    }

    public Mapping setWeaponId(int weaponId) {
        if (id == null) id = new CharacterWeaponId();
        id.setWeaponId(weaponId);
        return this;
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "id=" + id +
                '}';
    }
}
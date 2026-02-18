package victors3136.ubb.mfpc.model.mappings;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CharacterWeaponId implements Serializable {

    @Column(name = "character")
    private Integer characterId;

    @Column(name = "weapon")
    private Integer weaponId;

    public CharacterWeaponId() {
    }

    public CharacterWeaponId(Integer characterId, Integer weaponId) {
        this.characterId = characterId;
        this.weaponId = weaponId;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (
                o instanceof CharacterWeaponId that
                        && Objects.equals(characterId, that.characterId)
                        && Objects.equals(weaponId, that.weaponId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, weaponId);
    }

    @Override
    public String toString() {
        return "CharacterWeaponId{" +
                "characterId=" + characterId +
                ", weaponId=" + weaponId +
                '}';
    }
}

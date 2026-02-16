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
    private int characterId;

    @Column(name = "weapon")
    private int weaponId;

    public CharacterWeaponId() {
    }

    public CharacterWeaponId(int characterId, int weaponId) {
        this.characterId = characterId;
        this.weaponId = weaponId;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (
                o instanceof CharacterWeaponId that
                        && characterId == that.characterId
                        && weaponId == that.weaponId
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, weaponId);
    }
}

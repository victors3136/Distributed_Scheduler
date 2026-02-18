package victors3136.ubb.mfpc.model.characters;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "characters")
public class Character {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "display_name")
    private String name;

    @Column(name = "hp")
    private Integer hp;

    @Column(name = "attack_modifier")
    private Integer attackModifier;

    @Column(name = "defence_modifier")
    private Integer defenceModifier;

    public Character() {
    }

    @Override
    public final boolean equals(Object o) {
        return this == o || (
                o instanceof Character character
                        && Objects.equals(id, character.id)
                        && Objects.equals(hp, character.hp)
                        && Objects.equals(attackModifier, character.attackModifier)
                        && Objects.equals(defenceModifier, character.defenceModifier)
                        && name.equals(character.name)
        );
    }

    @Override
    public int hashCode() {
        return 31 * (
                31 * (
                        31 * (
                                31 * id + name.hashCode()
                        ) + hp
                ) + attackModifier
        ) + defenceModifier;
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hp=" + hp +
                ", attackModifier=" + attackModifier +
                ", defenceModifier=" + defenceModifier +
                '}';
    }
}

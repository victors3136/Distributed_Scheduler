package victors3136.ubb.mfpc.model.characters;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "characters")
public class Character {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "display_name")
    private String name;

    @Column(name = "hp")
    private int hp;

    @Column(name = "attack_modifier")
    private int attackModifier;

    @Column(name = "defence_modifier")
    private int defenceModifier;

    public Character() {
    }

    @Override
    public final boolean equals(Object o) {
        return this == o || (
                o instanceof Character character
                        && id == character.id
                        && hp == character.hp
                        && attackModifier == character.attackModifier
                        && defenceModifier == character.defenceModifier
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
}

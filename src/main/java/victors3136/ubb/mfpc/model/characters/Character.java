package victors3136.ubb.mfpc.model.characters;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import victors3136.ubb.mfpc.controller.requests.AddCharacterRequest;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public static Character fromAddRequest(AddCharacterRequest req) {
        var newCharacter = new Character();
        newCharacter.setName(req.displayName());
        newCharacter.setHp(req.hp());
        newCharacter.setAttackModifier(req.attackModifier());
        newCharacter.setDefenceModifier(req.defenceModifier());
        return newCharacter;
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

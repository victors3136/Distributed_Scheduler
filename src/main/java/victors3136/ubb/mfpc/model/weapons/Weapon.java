package victors3136.ubb.mfpc.model.weapons;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "weapons")
public class Weapon {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "display_name")
    private String name;

    @Column(name = "damage")
    private int damage;


    public Weapon() {
    }

    @Override
    public final boolean equals(Object o) {
        return this == o || (
                o instanceof Weapon weapon
                        && id == weapon.id
                        && damage == weapon.damage
                        && name.equals(weapon.name)
        );
    }

    @Override
    public int hashCode() {
        return 31 * (
                31 * id + name.hashCode()
        ) + damage;
    }
}

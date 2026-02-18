package victors3136.ubb.mfpc.model.weapons;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "weapons")
public class Weapon {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
                        && Objects.equals(id, weapon.id)
                        && Objects.equals(damage, weapon.damage)
                        && name.equals(weapon.name)
        );
    }

    @Override
    public int hashCode() {
        return 31 * (
                31 * id + name.hashCode()
        ) + damage;
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                '}';
    }
}

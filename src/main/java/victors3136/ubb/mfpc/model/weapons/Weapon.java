package victors3136.ubb.mfpc.model.weapons;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import victors3136.ubb.mfpc.controller.requests.AddWeaponRequest;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "weapons")
public class Weapon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "display_name")
    private String name;

    @Column(name = "damage")
    private int damage;


    public Weapon() {
    }

    public static Weapon fromAddRequest(AddWeaponRequest req) {
        var newWeapon = new Weapon();
        newWeapon.setName(req.displayName());
        newWeapon.setDamage(req.damage());
        System.out.println(newWeapon);
        return newWeapon;
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

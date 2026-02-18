package victors3136.ubb.mfpc.repository.weapons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import victors3136.ubb.mfpc.model.weapons.Weapon;


@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Integer> {
}

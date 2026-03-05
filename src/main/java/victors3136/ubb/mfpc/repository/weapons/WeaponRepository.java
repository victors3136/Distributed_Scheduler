package victors3136.ubb.mfpc.repository.weapons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import victors3136.ubb.mfpc.model.weapons.Weapon;

import java.util.Optional;


@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Integer> {

    @Query(value = """
            select *
            from weapons
            where display_name = :key
            """, nativeQuery = true)
    Optional<Weapon> getByLockKey(@Param("key") String key);
}

package victors3136.ubb.mfpc.repository.weapons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.weapons.Weapon;

import java.util.Optional;


@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Integer> {
    @Query(value = """
            select id
            from weapons
            where display_name = :name
            """, nativeQuery = true)
    Optional<Integer> getIdByName(@Param("name") String name);

    @Query(value = """
            select *
            from weapons
            where display_name = :name
            """, nativeQuery = true)
    Optional<Weapon> getByName(@Param("name") String name);
}

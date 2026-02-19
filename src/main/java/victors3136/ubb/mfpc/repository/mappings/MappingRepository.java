package victors3136.ubb.mfpc.repository.mappings;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;

import java.util.Optional;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, CharacterWeaponId> {

    @Query(value = """
            select *
            from mappings
            where character = :characterId and weapon = :weaponId
            """, nativeQuery = true)
    Optional<Mapping> get(@Param("characterId") int characterId, @Param("weaponId") int weaponId);
}

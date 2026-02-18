package victors3136.ubb.mfpc.repository.mappings;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.mappings.CharacterWeaponId;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, CharacterWeaponId> {
}

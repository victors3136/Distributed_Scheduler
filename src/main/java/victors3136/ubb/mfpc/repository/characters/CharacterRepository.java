package victors3136.ubb.mfpc.repository.characters;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import victors3136.ubb.mfpc.model.characters.Character;

import java.util.Optional;

@Repository
@Qualifier("characters")
public interface CharacterRepository extends JpaRepository<Character, Integer> {
    @Query(value = """
            select id
            from characters
            where display_name = :name
            """, nativeQuery = true)
    Optional<Integer> getIdByName(@Param("name") String name);

}

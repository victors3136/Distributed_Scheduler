package victors3136.ubb.mfpc.repository.characters;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import victors3136.ubb.mfpc.model.characters.Character;

@Repository
@Qualifier("characters")
public interface CharacterRepository extends JpaRepository<Character, Integer> {
}

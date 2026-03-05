package victors3136.ubb.mfpc.service.scheduling.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.repository.characters.CharacterRepository;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

import java.util.Optional;
import java.util.function.Supplier;


@Service
public class CharacterOperationService
        extends AbstractOperationService<String, Character, CharacterRepository> {

    @Autowired
    public CharacterOperationService(CharacterRepository repo) {
        super(repo, FutureResource::ofCharacter, "character");
    }

    @Override
    protected Optional<Character> getByKey(Supplier<String> key) {
        return repository.getByLockKey(key.get());
    }
}
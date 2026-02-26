package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.exceptions.ValidationException;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.repository.characters.CharacterRepository;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static victors3136.ubb.mfpc.service.scheduling.OperationCreationService.*;


@Service
public class CharacterOperationService implements OperationService<String, Character> {
    private final CharacterRepository characterRepository;

    @Autowired
    public CharacterOperationService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public Operation select(Supplier<String> characterName, Consumer<Character> sideEffect) {
        return selectOperation(
                FutureResource.ofCharacter(characterName),
                () -> characterRepository.getByName(characterName.get()).orElseThrow(() -> new ValidationException(
                        "No character named %s found.".formatted(characterName))
                ),
                sideEffect
        );
    }

    public Operation insert(Supplier<Character> character, Consumer<Character> sideEffect) {
        return insertOperation(
                FutureResource.ofCharacter(() -> character.get().getName()),
                character,
                characterRef -> {
                    characterRepository.save(characterRef);
                    sideEffect.accept(characterRef);
                },
                characterRepository::delete
        );
    }

    public Operation delete(Supplier<Character> character, Consumer<Character> sideEffect) {
        return deleteOperation(
                FutureResource.ofCharacter(() -> character.get().getName()),
                character,
                characterRef -> {
                    characterRepository.delete(characterRef);
                    sideEffect.accept(characterRef);
                },
                characterRepository::save
        );
    }

    public Operation update(Supplier<Character> newCharacter, Supplier<Character> oldCharacter, Consumer<Character> sideEffect) {
        return updateOperation(
                FutureResource.ofCharacter(() -> newCharacter.get().getName()),
                newCharacter,
                oldCharacter,
                characterRef -> {
                    assert characterRef.getClass() == Character.class;
                    var savedCharacterRef = characterRepository.save(characterRef);
                    assert savedCharacterRef.getClass() == Character.class;
                    sideEffect.accept(savedCharacterRef);
                },
                characterRepository::save
        );
    }
}


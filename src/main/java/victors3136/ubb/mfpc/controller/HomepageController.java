package victors3136.ubb.mfpc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victors3136.ubb.mfpc.repository.characters.CharacterRepository;
import victors3136.ubb.mfpc.repository.mappings.MappingRepository;
import victors3136.ubb.mfpc.repository.weapons.WeaponRepository;

@RestController
@CrossOrigin(origins = "*")
public class HomepageController {

    private final CharacterRepository characterRepository;
    private final MappingRepository mappingRepository;
    private final WeaponRepository weaponRepository;

    @Autowired
    public HomepageController(CharacterRepository characterRepository,
                              MappingRepository mappingRepository,
                              WeaponRepository weaponRepository
    ) {
        this.characterRepository = characterRepository;
        this.mappingRepository = mappingRepository;
        this.weaponRepository = weaponRepository;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello, MFPC!<br>"
                + "<br>C:<br>" + characterRepository.findAll().stream().map(Object::toString).reduce("<br>", (a, b) -> a + b)
                + "<br>M:<br>" + mappingRepository.findAll().stream().map(Object::toString).reduce("<br>", (a, b) -> a + b)
                + "<br>W:<br>" + weaponRepository.findAll().stream().map(Object::toString).reduce("<br>", (a, b) -> a + b)
        );
    }
}
package victors3136.ubb.mfpc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import victors3136.ubb.mfpc.controller.requests.*;
import victors3136.ubb.mfpc.controller.responses.CharacterWeaponStats;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.service.scheduling.TransactionExecutorService;
import victors3136.ubb.mfpc.utils.ResultWithPossibleException;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/submit")
public class ApiController {

    private final TransactionExecutorService service;

    @Autowired
    public ApiController(TransactionExecutorService service) {
        this.service = service;
    }

    @PostMapping("/character")
    ResponseEntity<ResultWithPossibleException<Character>> addCharacter(@RequestBody AddCharacterRequest req) {
        var result = service.addCharacter(req);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/weapon")
    ResponseEntity<ResultWithPossibleException<Weapon>> addWeapon(@RequestBody AddWeaponRequest req) {
        var result = service.addWeapon(req);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/mapping")
    ResponseEntity<ResultWithPossibleException<Mapping>> addMapping(@RequestBody AddMappingRequest req) {
        var result = service.addMapping(req);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/mapping")
    ResponseEntity<Void> dropMapping(@RequestBody DropMappingRequest req) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/attack")
    ResponseEntity<Void> attack(@RequestBody AttackRequest req) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/character")
    ResponseEntity<Character> getCharacter(@RequestBody ReadCharacterStats req) {
        var c = new Character();
        c.setName(req.characterName());
        return ResponseEntity.ok(c);
    }

    @PostMapping("/characterweapons")
    ResponseEntity<CharacterWeaponStats> getCharacterWeapons(@RequestBody ReadCharacterWeaponStats req) {
        var c = new CharacterWeaponStats(req.characterName(), List.of());
        return ResponseEntity.ok(c);
    }
}


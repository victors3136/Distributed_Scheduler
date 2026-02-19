package victors3136.ubb.mfpc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import victors3136.ubb.mfpc.controller.requests.*;
import victors3136.ubb.mfpc.controller.responses.AttackMultipleSummary;
import victors3136.ubb.mfpc.controller.responses.AttackSummary;
import victors3136.ubb.mfpc.controller.responses.CharacterWeaponStats;
import victors3136.ubb.mfpc.controller.responses.HealingSummary;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;
import victors3136.ubb.mfpc.service.scheduling.MainService;
import victors3136.ubb.mfpc.exceptions.ResultWithPossibleException;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/submit")
public class ApiController {

    private final MainService service;

    @Autowired
    public ApiController(MainService service) {
        this.service = service;
    }

    @PostMapping("/character")
    ResponseEntity<ResultWithPossibleException<Character>> addCharacter(@RequestBody AddCharacterRequest req) {
        return ResponseEntity.ok(service.addCharacter(req));
    }

    @PostMapping("/weapon")
    ResponseEntity<ResultWithPossibleException<Weapon>> addWeapon(@RequestBody AddWeaponRequest req) {
        return ResponseEntity.ok(service.addWeapon(req));
    }

    @PostMapping("/mapping")
    ResponseEntity<ResultWithPossibleException<Mapping>> addMapping(@RequestBody AddMappingRequest req) {
        return ResponseEntity.ok(service.addMapping(req));
    }

    @DeleteMapping("/mapping")
    ResponseEntity<ResultWithPossibleException<Mapping>> dropMapping(@RequestBody DropMappingRequest req) {
        return ResponseEntity.ok(service.dropMapping(req));
    }

    @PostMapping("/attack")
    ResponseEntity<ResultWithPossibleException<AttackSummary>> attack(@RequestBody AttackRequest req) {
        return ResponseEntity.ok(service.attack(req));
    }
    @PostMapping("/heal")
    ResponseEntity<ResultWithPossibleException<HealingSummary>> attack(@RequestBody HealCharacterRequest req) {
        return ResponseEntity.ok(service.heal(req));
    }


    @PostMapping("/attackMultiple")
    ResponseEntity<ResultWithPossibleException<AttackMultipleSummary>> attackMultiple(@RequestBody AttackMultipleRequest req) {
        return ResponseEntity.ok(service.attackMultiple(req));
    }

    @GetMapping("/character")
    ResponseEntity<ResultWithPossibleException<Character>> getCharacter(@RequestBody ReadCharacterStats req) {
        return ResponseEntity.ok(service.getCharacter(req));
    }


    @GetMapping("/weapon")
    ResponseEntity<ResultWithPossibleException<Weapon>> getCharacter(@RequestBody ReadWeaponStats req) {
        return ResponseEntity.ok(service.getWeapon(req));
    }

}


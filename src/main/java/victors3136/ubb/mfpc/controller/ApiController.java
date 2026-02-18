package victors3136.ubb.mfpc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import victors3136.ubb.mfpc.controller.requests.*;
import victors3136.ubb.mfpc.controller.responses.CharacterWeaponStats;
import victors3136.ubb.mfpc.model.characters.Character;
import victors3136.ubb.mfpc.model.mappings.Mapping;
import victors3136.ubb.mfpc.model.weapons.Weapon;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/submit")
public class ApiController {

    @PostMapping("/character")
    ResponseEntity<Character> addCharacter(@RequestBody AddCharacterRequest req) {
        var c = new Character();
        c.setHp(req.hp());
        c.setName(req.displayName());
        c.setAttackModifier(req.attackModifier());
        c.setDefenceModifier(req.defenceModifier());
        return ResponseEntity.ok(c);
    }

    @PostMapping("/weapon")
    ResponseEntity<Weapon> addWeapon(@RequestBody AddWeaponRequest req) {
        var w = new Weapon();
        w.setDamage(req.damage());
        w.setName(req.displayName());
        return ResponseEntity.ok(w);
    }

    @PostMapping("/mapping")
    ResponseEntity<Mapping> addMapping(@RequestBody AddMappingRequest req) {
        var m = new Mapping();
        m.setCharacterId(req.characterName().hashCode());
        m.setWeaponId(req.weaponName().hashCode());
        return ResponseEntity.ok(m);
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


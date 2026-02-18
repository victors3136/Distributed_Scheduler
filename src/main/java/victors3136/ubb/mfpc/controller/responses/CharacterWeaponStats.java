package victors3136.ubb.mfpc.controller.responses;

import java.util.List;

public record CharacterWeaponStats(String characterName, List<WeaponDamagePair> entries) {
}

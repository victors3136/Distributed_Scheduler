package victors3136.ubb.mfpc.controller.requests;

import java.util.List;

public record AttackMultipleRequest(
        String attackerName,
        String weaponName,
        List<String> targetNames) {
}

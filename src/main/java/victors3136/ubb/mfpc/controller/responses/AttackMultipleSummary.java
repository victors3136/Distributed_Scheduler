package victors3136.ubb.mfpc.controller.responses;

import java.util.List;

public record AttackMultipleSummary(String attackerName, String weaponName, List<AttackSummary> stats) {
}

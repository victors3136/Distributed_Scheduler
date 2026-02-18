package victors3136.ubb.mfpc.controller.requests;

public record AttackRequest(
        String attackerName,
        String weaponName,
        String targetName) {
}

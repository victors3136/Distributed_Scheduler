package victors3136.ubb.mfpc.controller.requests;

public record AddCharacterRequest(
        String displayName,
        int hp,
        int attackModifier,
        int defenceModifier) {
}



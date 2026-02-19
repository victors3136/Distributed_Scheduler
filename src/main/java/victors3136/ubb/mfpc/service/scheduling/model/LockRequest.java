package victors3136.ubb.mfpc.service.scheduling.model;

import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;

import java.util.UUID;

public class LockRequest {

    UUID transactionId;
    LockType type;

    public LockRequest(UUID transactionId, LockType type) {
        this.transactionId = transactionId;
        this.type = type;
    }
}

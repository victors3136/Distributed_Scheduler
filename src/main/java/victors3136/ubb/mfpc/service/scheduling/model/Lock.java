package victors3136.ubb.mfpc.service.scheduling.model;

import lombok.Getter;
import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;

import java.util.UUID;

@Getter
public class Lock {

    UUID ownerTransactionId;
    LockType type;

    public Lock(UUID ownerTransactionId, LockType type) {
        this.ownerTransactionId = ownerTransactionId;
        this.type = type;
    }
}

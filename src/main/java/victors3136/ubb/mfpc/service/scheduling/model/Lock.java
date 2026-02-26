package victors3136.ubb.mfpc.service.scheduling.model;

import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;

import java.util.UUID;

public record Lock(UUID ownerTransactionId, LockType type) {
}

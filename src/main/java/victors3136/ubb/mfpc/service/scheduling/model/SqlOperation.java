package victors3136.ubb.mfpc.service.scheduling.model;

import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;

public record SqlOperation(
        Resource resource,
        LockType lockType,
        Runnable doAction,
        Runnable undoAction
) {
    @Override
    public String toString() {
        return "SqlOperation{" +
                "lockType=" + lockType +
                ", resource=" + resource +
                '}';
    }
}

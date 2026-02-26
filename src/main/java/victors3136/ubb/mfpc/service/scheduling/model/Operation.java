package victors3136.ubb.mfpc.service.scheduling.model;

import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

public record Operation(
        FutureResource resource,
        LockType lockType,
        Runnable doAction,
        Runnable undoAction,
        String description
) {
    @Override
    public String toString() {
        return "SqlOperation{" +
                "lockType=" + lockType +
                ", resource=" + resource +
                '}';
    }
}

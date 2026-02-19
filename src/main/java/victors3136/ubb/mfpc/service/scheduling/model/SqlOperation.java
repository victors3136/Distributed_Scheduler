package victors3136.ubb.mfpc.service.scheduling.model;

import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;

import java.sql.SQLException;

public record SqlOperation(
        Resource resource,
        LockType lockType,
        Runnable doAction,
        Runnable undoAction
) {
}

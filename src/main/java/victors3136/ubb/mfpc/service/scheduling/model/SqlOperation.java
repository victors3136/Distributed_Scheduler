package victors3136.ubb.mfpc.service.scheduling.model;

import lombok.Getter;
import lombok.Setter;
import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;

@Setter
@Getter
public class SqlOperation {
    Resource resource;
    LockType lockType;
    Runnable execute;
    Runnable undo;
}

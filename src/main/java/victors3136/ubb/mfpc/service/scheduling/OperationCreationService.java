package victors3136.ubb.mfpc.service.scheduling;

import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;
import victors3136.ubb.mfpc.service.scheduling.model.resources.Resource;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.LockType.*;

public class OperationCreationService {

    static private final Runnable NoUndoActionRequired = () -> {
    };

    static <T> Operation selectOperation(FutureResource resource,
                                         Supplier<T> action,
                                         Consumer<T> sideEffect) {
        return new Operation(
                resource,
                Read,
                () -> sideEffect.accept(action.get()),
                NoUndoActionRequired
        );
    }

    static <T> Operation insertOperation(FutureResource resource,
                                         Supplier<T> entity,
                                         Consumer<T> save,
                                         Consumer<T> remove) {
        return new Operation(
                resource,
                Write,
                () -> save.accept(entity.get()),
                () -> remove.accept(entity.get())
        );
    }

    static <T> Operation deleteOperation(FutureResource resource,
                                         Supplier<T> entity,
                                         Consumer<T> remove,
                                         Consumer<T> restore) {
        return new Operation(
                resource,
                Write,
                () -> remove.accept(entity.get()),
                () -> restore.accept(entity.get())
        );
    }

    static <T> Operation updateOperation(FutureResource resource,
                                         Supplier<T> newEntity,
                                         Supplier<T> oldEntity,
                                         Consumer<T> update,
                                         Consumer<T> restore) {
        return new Operation(
                resource,
                Write,
                () -> update.accept(newEntity.get()),
                () -> restore.accept(oldEntity.get())
        );
    }
}

package victors3136.ubb.mfpc.service.scheduling.operations;

import victors3136.ubb.mfpc.service.scheduling.model.Operation;
import victors3136.ubb.mfpc.service.scheduling.model.resources.FutureResource;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.LockType.*;

public class OperationCreationService {

    static private final Runnable NoUndoActionRequired = () -> {
    };

    static <EntityType> Operation selectOperation(FutureResource resource,
                                                  Supplier<EntityType> action,
                                                  Consumer<EntityType> sideEffect,
                                                  String description) {
        return new Operation(
                resource,
                Read,
                () -> sideEffect.accept(action.get()),
                NoUndoActionRequired,
                "Select " + description
        );
    }

    static <EntityType> Operation insertOperation(FutureResource resource,
                                         Supplier<EntityType> entity,
                                         Consumer<EntityType> save,
                                         Consumer<EntityType> remove,
                                         String description) {
        return new Operation(
                resource,
                Write,
                () -> save.accept(entity.get()),
                () -> remove.accept(entity.get()),
                "Insert " + description
        );
    }

    static <EntityType> Operation deleteOperation(FutureResource resource,
                                         Supplier<EntityType> entity,
                                         Consumer<EntityType> remove,
                                         Consumer<EntityType> restore,
                                         String description) {
        return new Operation(
                resource,
                Write,
                () -> remove.accept(entity.get()),
                () -> restore.accept(entity.get()),
                "Delete " + description
        );
    }

    static <EntityType> Operation updateOperation(FutureResource resource,
                                         Supplier<EntityType> newEntity,
                                         Supplier<EntityType> oldEntity,
                                         Consumer<EntityType> update,
                                         Consumer<EntityType> restore,
                                         String description) {
        return new Operation(
                resource,
                Write,
                () -> update.accept(newEntity.get()),
                () -> restore.accept(oldEntity.get()),
                "Update " + description
        );
    }
}

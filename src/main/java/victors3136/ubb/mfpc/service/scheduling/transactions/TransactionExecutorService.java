package victors3136.ubb.mfpc.service.scheduling.transactions;

import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.exceptions.Result;
import victors3136.ubb.mfpc.service.scheduling.LockService;
import victors3136.ubb.mfpc.service.scheduling.operations.OperationServiceRegistry;
import victors3136.ubb.mfpc.service.scheduling.model.Transaction;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Vector;
import java.util.function.Supplier;

@Service
public class TransactionExecutorService {
    private final LockService lockService;
    public final List<Transaction> Transactions = new Vector<>();
    private final OperationServiceRegistry registry;

    TransactionExecutorService(LockService lockService, OperationServiceRegistry registry) {
        this.lockService = lockService;
        this.registry = registry;
    }

    public <T> Result<T> submit(Transaction transaction, Supplier<T> result) {
        Transactions.add(transaction);
        var undoStack = new ArrayDeque<Runnable>();
        try {
            while (!transaction.completedAllOperations()) {
                var operation = transaction.getNextOperation();
                System.out.println(transaction);
                lockService.waitToLock(
                        transaction.getId(),
                        operation.resource(),
                        operation.lockType()
                );
                operation.doAction().run();
                undoStack.push(operation.undoAction());
                //noinspection BusyWait
                Thread.sleep(1_000);
            }
            transaction.markCommited();
            return Result.withSucces(result.get());
        } catch (Exception exceptionCause) {
            while (!undoStack.isEmpty()) {
                var undoAction = undoStack.pop();
                undoAction.run();
            }
            transaction.markAborted();
            return Result.withFailure(exceptionCause);
        } finally {
            assert !transaction.isActive();
            lockService.releaseAllLocks(transaction.getId());
            System.out.println(transaction);
        }
    }

    public TransactionBuilder newTransaction() {
        return new TransactionBuilder(new Transaction(), registry);
    }

    public <EntityType> Result<EntityType> submit(TransactionBuilder builder, Supplier<EntityType> result) {
        return submit(builder.build(), result);
    }
}

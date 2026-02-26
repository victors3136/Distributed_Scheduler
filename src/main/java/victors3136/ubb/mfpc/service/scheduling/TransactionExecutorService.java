package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.exceptions.Result;
import victors3136.ubb.mfpc.service.scheduling.model.Transaction;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TransactionExecutorService {
    private final LockService lockService;
    public final List<Transaction> Transactions = new Vector<>();

    TransactionExecutorService(LockService lockService) {
        this.lockService = lockService;
    }

    public <T> Result<T> submit(Transaction transaction, Supplier<T> result) {
        Transactions.add(transaction);
        var undoStack = new ArrayDeque<Runnable>();
        try {
            for (var operation : transaction.getOperations()) {
                lockService.waitToLock(
                        transaction.getId(),
                        operation.resource(),
                        operation.lockType()
                );
                operation.doAction().run();
                Thread.sleep(3_000);
                undoStack.push(operation.undoAction());
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
            System.out.println(Transactions.stream().map(Objects::toString).collect(Collectors.joining("\n")));
        }
    }

}

package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.exceptions.ResultWithPossibleException;
import victors3136.ubb.mfpc.service.scheduling.model.Operation;
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

    public <T> ResultWithPossibleException<T> submit(Transaction transaction, Supplier<T> result) {
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
            return ResultWithPossibleException.success(result.get());
        } catch (Exception e) {
            while (!undoStack.isEmpty()) {
                var nextAction = undoStack.pop();
                nextAction.run();
            }
            transaction.markAborted();
            return ResultWithPossibleException.failure(e);
        } finally {
            assert !transaction.isActive();
            lockService.releaseAllLocks(transaction.getId());
            System.out.println(Transactions.stream().map(Objects::toString).collect(Collectors.joining("\n")));
        }
    }

}

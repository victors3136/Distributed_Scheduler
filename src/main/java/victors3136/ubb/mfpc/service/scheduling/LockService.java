package victors3136.ubb.mfpc.service.scheduling;

import org.springframework.stereotype.Service;
import victors3136.ubb.mfpc.service.scheduling.model.Lock;
import victors3136.ubb.mfpc.service.scheduling.model.Resource;
import victors3136.ubb.mfpc.service.scheduling.model.enums.LockType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.LockType.*;

@Service
public class LockService {

    private final ConcurrentHashMap<Resource, List<Lock>> lockTable =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<UUID, Set<UUID>> waitForGraph =
            new ConcurrentHashMap<>();

    private final ReentrantLock managerLock = new ReentrantLock(true);
    private final Condition someLockBecomesAvailable = managerLock.newCondition();

    private boolean isCompatible(Collection<Lock> existingLocks,
                                 UUID requestingTransactionId,
                                 LockType requested) {
        if (existingLocks.isEmpty()) return true;
        if (existingLocks.stream().allMatch(lock ->
                lock.getOwnerTransactionId().equals(requestingTransactionId))
        ) {
            return true;
        }
        if (requested == Read) {
            return existingLocks.stream().allMatch(lock ->
                    lock.getType() == Read
            );
        }
        return false;
    }

    private boolean canUpgrade(Collection<Lock> existingLocks, UUID requestingTransactionId) {
        return existingLocks.stream().allMatch(lock ->
                lock.getOwnerTransactionId().equals(requestingTransactionId)
        );
    }

    public void waitToLock(UUID requestingTransactionId, Resource resource, LockType lockType)
            throws DeadlockException {
        managerLock.lock();
        try {
            while (true) {
                var existingLocks = lockTable.computeIfAbsent(resource, _ -> new ArrayList<>());
                if (isCompatible(existingLocks, requestingTransactionId, lockType)) {
                    existingLocks.add(new Lock(requestingTransactionId, lockType));
                    waitForGraph.remove(requestingTransactionId);
                    return;
                }
                if (canUpgrade(existingLocks, requestingTransactionId)) {
                    existingLocks.removeIf(lock ->
                            lock.getOwnerTransactionId().equals(requestingTransactionId)
                    );
                    existingLocks.add(new Lock(requestingTransactionId, Write));
                    return;
                }
                addWaitEdges(requestingTransactionId, existingLocks);
                if (detectDeadlock(requestingTransactionId)) {
                    throw new DeadlockException("Deadlock detected for transaction " + requestingTransactionId);
                }
                someLockBecomesAvailable.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            managerLock.unlock();
        }
    }

    private void addWaitEdges(UUID waitingTransactionId, Collection<Lock> owners) {
        var edges = waitForGraph.computeIfAbsent(waitingTransactionId, _ -> new HashSet<>());
        for (var owner : owners) {
            if (owner.getOwnerTransactionId().equals(waitingTransactionId)) {
                continue;
            }
            edges.add(owner.getOwnerTransactionId());

        }
    }

    private boolean detectDeadlock(UUID startTransactionId) {
        var visited = new HashSet<UUID>();
        return dfs(startTransactionId, startTransactionId, visited);
    }

    private boolean dfs(UUID current, UUID target, Set<UUID> visited) {
        if (!visited.add(current)) return false;
        var neighbors = waitForGraph.getOrDefault(current, Set.of());
        for (var next : neighbors) {
            if (next.equals(target)) return true;
            if (dfs(next, target, visited)) return true;
        }
        return false;
    }

    public void releaseAllLocks(UUID transactionId) {
        managerLock.lock();
        try {
            for (var entries : lockTable.values()) {
                entries.removeIf(lock ->
                        lock.getOwnerTransactionId().equals(transactionId)
                );
            }
            waitForGraph.remove(transactionId);
            for (var edges : waitForGraph.values()) {
                edges.remove(transactionId);
            }
            someLockBecomesAvailable.signalAll();
        } finally {
            managerLock.unlock();
        }
    }
}

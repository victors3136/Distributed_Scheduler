package victors3136.ubb.mfpc.service.scheduling.model;

import lombok.Getter;
import lombok.Setter;
import victors3136.ubb.mfpc.service.scheduling.model.enums.TransactionStatus;

import java.time.Instant;
import java.util.*;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.TransactionStatus.*;


@Getter
@Setter
public final class Transaction {
    private final UUID id;
    private final Queue<Operation> pendingOperations;
    private final List<Operation> completedOperations;
    private final Instant timestamp;
    private TransactionStatus status = Active;

    public Transaction(UUID id, Queue<Operation> pendingOperations, Instant timestamp) {
        this.id = id;
        this.pendingOperations = pendingOperations;
        this.timestamp = timestamp;
        this.completedOperations = new ArrayList<>();
    }

    public Transaction() {
        this(UUID.randomUUID(), new ArrayDeque<>(), Instant.now());
    }

    public void enqueue(Operation... operations) {
        this.pendingOperations.addAll(List.of(operations));
    }

    public boolean completedAllOperations() {
        return pendingOperations.isEmpty();
    }

    public Operation getNextOperation() {
        var next = pendingOperations.poll();
        completedOperations.add(next);
        return next;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Transaction) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.pendingOperations, that.pendingOperations) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, pendingOperations, timestamp);
    }

    @Override
    public String toString() {
        return switch (status) {
            case Active -> "%s:\n\tto do: [%s]".formatted(status,
                    pendingOperations.stream().map(Operation::description).reduce("%s | %s"::formatted).orElse(""));
            case Commited -> "%s:\n\tdone: [%s]".formatted(status,
                    completedOperations.stream().map(Operation::description).reduce("%s | %s"::formatted).orElse(""));
            case Aborted -> "%s: \n\tto undo: [%s]\n\tpending: [%s]".formatted(status,
                    completedOperations.stream().map(Operation::description).reduce("%s | %s"::formatted).orElse(""),
                    pendingOperations.stream().map(Operation::description).reduce("%s | %s"::formatted).orElse(""));
        };
    }

    public void markCommited() {
        if (status != Active) throw new RuntimeException("Attempting to update already-completed transaction");
        status = Commited;
    }

    public void markAborted() {
        if (status != Active) throw new RuntimeException("Attempting to update already-completed transaction");
        status = Aborted;
    }

    public boolean isActive() {
        return status == Active;
    }
}
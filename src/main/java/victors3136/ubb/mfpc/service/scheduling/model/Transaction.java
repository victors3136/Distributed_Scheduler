package victors3136.ubb.mfpc.service.scheduling.model;

import lombok.Getter;
import lombok.Setter;
import victors3136.ubb.mfpc.service.scheduling.model.enums.TransactionStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static victors3136.ubb.mfpc.service.scheduling.model.enums.TransactionStatus.*;


@Getter
@Setter
public final class Transaction {
    private final UUID id;
    private final List<SqlOperation> operations;
    private final Instant timestamp;
    private TransactionStatus status = Active;

    public Transaction(UUID id, List<SqlOperation> operations, Instant timestamp) {
        this.id = id;
        this.operations = operations;
        this.timestamp = timestamp;
    }

    public Transaction() {
        this(UUID.randomUUID(), new ArrayList<>(), Instant.now());
    }

    public void addOperations(SqlOperation... operations) {
        this.operations.addAll(List.of(operations));
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Transaction) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.operations, that.operations) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, operations, timestamp);
    }

    @Override
    public String toString() {
        return "Transaction[" +
                "id=" + id + ", " +
                "status=" + status + ", " +
                "operations=" + operations + ", " +
                "timestamp=" + timestamp + ']';
    }

    public void markCommited() {
        assert status == Active : "Attempting to update already-completed transaction";
        status = Commited;
    }

    public void markAborted() {
        assert status == Active : "Attempting to update already-completed transaction";
        status = Aborted;
    }

    public boolean isActive() {
        return status == Active;
    }
}
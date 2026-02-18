package victors3136.ubb.mfpc.service.scheduling.model;

import lombok.Getter;
import lombok.Setter;
import victors3136.ubb.mfpc.service.scheduling.model.enums.TransactionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class Transaction {
    private UUID id;
    private Instant timestamp;
    private TransactionStatus status;
    private List<SqlOperation> operations;
}

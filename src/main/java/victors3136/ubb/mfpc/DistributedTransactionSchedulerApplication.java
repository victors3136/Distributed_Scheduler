package victors3136.ubb.mfpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class DistributedTransactionSchedulerApplication {

    static void main(String[] args) {
        SpringApplication.run(DistributedTransactionSchedulerApplication.class, args);
    }

}

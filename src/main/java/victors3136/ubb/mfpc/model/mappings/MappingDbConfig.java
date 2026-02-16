package victors3136.ubb.mfpc.model.mappings;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "victors3136.ubb.mfpc.model.mappings",
        entityManagerFactoryRef = "mappingEntityManager",
        transactionManagerRef = "mappingTransactionManager"
)
public class MappingDbConfig {

    @Bean
    @ConfigurationProperties("db2.datasource")
    public DataSource mappingDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mappingEntityManager")
    public LocalContainerEntityManagerFactoryBean mappingEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mappingDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("victors3136.ubb.mfpc.model.mappings")
                .persistenceUnit("mappings")
                .build();
    }

    @Bean(name = "mappingTransactionManager")
    public PlatformTransactionManager mappingTransactionManager(
            @Qualifier("mappingEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
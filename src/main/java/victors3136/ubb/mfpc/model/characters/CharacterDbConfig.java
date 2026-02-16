package victors3136.ubb.mfpc.model.characters;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "victors3136.ubb.mfpc.model.characters",
        entityManagerFactoryRef = "characterEntityManager",
        transactionManagerRef = "characterTransactionManager"
)
public class CharacterDbConfig {
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "db1.datasource")
    public DataSource characterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "characterEntityManager")
    public LocalContainerEntityManagerFactoryBean characterEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier("characterDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("victors3136.ubb.mfpc.model.characters")
                .persistenceUnit("characters")
                .build();
    }

    @Bean(name = "characterTransactionManager")
    public PlatformTransactionManager characterTransactionManager(
            @Qualifier("characterEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
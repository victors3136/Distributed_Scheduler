package victors3136.ubb.mfpc.config.characters;


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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "characterEntityManagerFactory",
        transactionManagerRef = "characterTransactionManager",
        basePackages = {"victors3136.ubb.mfpc.repository.characters"}
)
public class CharacterConfig {
    @Bean(name = "characterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.characters")
    public DataSource characterDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "characterEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean characterEntityManagerFactory(
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
            @Qualifier("characterEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

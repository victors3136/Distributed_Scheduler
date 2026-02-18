package victors3136.ubb.mfpc.config.weapons;

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
        entityManagerFactoryRef = "weaponEntityManagerFactory",
        transactionManagerRef = "weaponTransactionManager",
        basePackages = {"victors3136.ubb.mfpc.repository.weapons"}
)
public class WeaponConfig {
    @Bean(name = "weaponDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.weapons")
    public DataSource weaponDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "weaponEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean weaponEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("weaponDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("victors3136.ubb.mfpc.model.weapons")
                .persistenceUnit("weapons")
                .build();
    }

    @Bean(name = "weaponTransactionManager")
    public PlatformTransactionManager weaponTransactionManager(
            @Qualifier("weaponEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

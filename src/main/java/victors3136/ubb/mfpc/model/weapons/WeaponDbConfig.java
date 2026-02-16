package victors3136.ubb.mfpc.model.weapons;

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
        basePackages = "victors3136.ubb.mfpc.model.weapons",
        entityManagerFactoryRef = "weaponEntityManager",
        transactionManagerRef = "weaponTransactionManager"
)
public class WeaponDbConfig {

    @Bean
    @ConfigurationProperties("db3.datasource")
    public DataSource weaponDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "weaponEntityManager")
    public LocalContainerEntityManagerFactoryBean weaponEntityManager(
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
            @Qualifier("weaponEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

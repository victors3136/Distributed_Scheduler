package victors3136.ubb.mfpc.config.weapons;

import com.zaxxer.hikari.HikariDataSource;
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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
    @ConfigurationProperties(prefix = "app.datasource.weapons")
    public DataSource weaponDataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "weaponEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean weaponEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("weaponDataSource") DataSource dataSource) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean emf = builder
                .dataSource(dataSource)
                .packages("victors3136.ubb.mfpc.model.weapons")
                .persistenceUnit("Weapon")
                .build();

        emf.setJpaVendorAdapter(vendorAdapter);

        return emf;
    }

    @Bean(name = "weaponTransactionManager")
    public PlatformTransactionManager weaponTransactionManager(
            @Qualifier("weaponEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

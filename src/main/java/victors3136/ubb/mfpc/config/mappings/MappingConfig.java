package victors3136.ubb.mfpc.config.mappings;

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
        entityManagerFactoryRef = "mappingEntityManagerFactory",
        transactionManagerRef = "mappingTransactionManager",
        basePackages = {"victors3136.ubb.mfpc.repository.mappings"}
)
public class MappingConfig {
    @Bean(name = "mappingDataSource")
    @ConfigurationProperties(prefix = "app.datasource.mappings")
    public DataSource mappingDataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "mappingEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mappingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mappingDataSource") DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean emf = builder
                .dataSource(dataSource)
                .packages("victors3136.ubb.mfpc.model.mappings")
                .persistenceUnit("Mapping")
                .build();

        emf.setJpaVendorAdapter(vendorAdapter);

        return emf;
    }

    @Bean(name = "mappingTransactionManager")
    public PlatformTransactionManager mappingTransactionManager(
            @Qualifier("mappingEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

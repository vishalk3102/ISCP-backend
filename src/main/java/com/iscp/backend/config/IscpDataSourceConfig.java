package com.iscp.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.iscp.backend.repositories",
        entityManagerFactoryRef = "iscpEntityManagerFactory",
        transactionManagerRef = "iscpTransactionManager"
)
public class IscpDataSourceConfig {

    @Value("${spring.datasource.iscp.url}")
    private String url;

    @Value("${spring.datasource.iscp.username}")
    private String username;

    @Value("${spring.datasource.iscp.password}")
    private String password;

    @Value("${spring.datasource.iscp.driver-class-name}")
    private String driverClassName;


    @Primary
    @Bean(name = "iscpDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.iscp")
    public DataSource iscpDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();

        // Explicitly set properties to ensure they are picked up from properties files
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        return dataSource;
    }

    @Primary
//    @Bean(name = "iscpEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean iscpEntityManagerFactory(
//            EntityManagerFactoryBuilder builder, @Qualifier("iscpDataSource") DataSource dataSource) {
//        return builder
//                .dataSource(dataSource)
//                .packages("com.iscp.backend.models")
//                .persistenceUnit("iscp")
//                .build();
//    }

    @Bean(name = "iscpEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean iscpEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("iscpDataSource") DataSource dataSource) {

        // Hibernate properties
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");  // You can change this to "create" or "create-drop" if needed
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.boot.allow_jdbc_metadata_access", "false");

        // Set up the entity manager factory
        return builder
                .dataSource(dataSource)
                .packages("com.iscp.backend.models")  // Change this to your model package
                .persistenceUnit("iscp")
                .properties(jpaProperties)  // Add Hibernate properties
                .build();
    }

    @Primary
    @Bean(name = "iscpTransactionManager")
    public PlatformTransactionManager iscpTransactionManager(
            @Qualifier("iscpEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
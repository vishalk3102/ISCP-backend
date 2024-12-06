package com.iscp.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
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
        basePackages = "com.iscp.backend.mims.repositories",
        entityManagerFactoryRef = "mimsEntityManagerFactory",
        transactionManagerRef = "mimsTransactionManager"
)
public class MimsDataSourceConfig {

        // DataSource Bean for MIMS
        @Bean(name = "mimsDataSource")
        @ConfigurationProperties(prefix = "spring.datasource.mims")
        public DataSource mimsDataSource() {
                HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create().build();
                dataSource.setJdbcUrl("jdbc:sqlserver://172.16.1.54:1433;DatabaseName=MIMS_Masked_2023_New;encrypt=true;trustServerCertificate=true;integratedSecurity=false;");
                dataSource.setUsername("sa");
                dataSource.setPassword("123456");
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                return dataSource;

        }

        // EntityManagerFactory Bean for MIMS
        @Bean(name = "mimsEntityManagerFactory")
        public LocalContainerEntityManagerFactoryBean mimsEntityManagerFactory(
                EntityManagerFactoryBuilder builder,
                @Qualifier("mimsDataSource") DataSource dataSource) {
                return builder
                        .dataSource(dataSource)
                        .packages("com.iscp.backend.mims.model") // Update this to match your model/entities package
                        .persistenceUnit("mims")  // Name of the persistence unit
                        .build();
        }

        // TransactionManager Bean for MIMS
        @Bean(name = "mimsTransactionManager")
        public PlatformTransactionManager mimsTransactionManager(
                @Qualifier("mimsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
                return new JpaTransactionManager(entityManagerFactory);
        }
}

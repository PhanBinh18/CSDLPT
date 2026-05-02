package com.example.demo.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.demo.repository", // Thư mục chứa các file Repository
        transactionManagerRef = "transactionManager",
        entityManagerFactoryRef = "entityManagerFactory"
)
public class DataSourceConfig {

    // 1. Khởi tạo DataSource Miền Bắc (Đọc từ yaml)
    @Bean
    @ConfigurationProperties(prefix = "site.mienbac")
    public DataSource mienBacDataSource() {
        return DataSourceBuilder.create().build();
    }

    // 2. Khởi tạo DataSource Miền Nam (Đọc từ yaml)
    @Bean
    @ConfigurationProperties(prefix = "site.miennam")
    public DataSource mienNamDataSource() {
        return DataSourceBuilder.create().build();
    }

    // 3. Đưa 2 kết nối vào bộ điều phối RoutingDataSource
    @Bean
    @Primary // Bắt buộc có để Spring biết đây là DataSource chính
    public DataSource routingDataSource() {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(SiteEnum.MIEN_BAC, mienBacDataSource());
        targetDataSources.put(SiteEnum.MIEN_NAM, mienNamDataSource());

        routingDataSource.setTargetDataSources(targetDataSources);

        // Mặc định nếu không set context gì thì sẽ trỏ về Kho Miền Bắc
        routingDataSource.setDefaultTargetDataSource(mienBacDataSource());

        return routingDataSource;
    }

    // 4. Cấu hình EntityManagerFactory cho JPA
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(routingDataSource());
        em.setPackagesToScan("com.example.demo.entity"); // Thư mục chứa các class @Entity

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        // Sử dụng Dialect của MySQL để Hibernate sinh code SQL cho chuẩn
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.format_sql", "true"); // Format SQL in ra console cho dễ nhìn
        em.setJpaPropertyMap(properties);

        return em;
    }

    // 5. Cấu hình Quản lý Giao dịch (Transaction)
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}

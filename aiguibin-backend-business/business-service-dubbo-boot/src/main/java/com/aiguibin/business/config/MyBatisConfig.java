package com.aiguibin.business.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * @author AIguibin
 * Date time 2019年05月07日 09:54:07
 */
@Configuration
@EnableTransactionManagement
@PropertySource({"classpath:datasource.properties"})
@MapperScan(basePackages = {"com.aiguibin.business.mapper"})
@ComponentScan(basePackages = {"com.aiguibin.business.mapper"})
public class MyBatisConfig implements TransactionManagementConfigurer {
    @Value("${jdbc.driverClass}")
    private String driverClass;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.jdbcUrl}")
    private String jdbcUrl;

    private DataSource dataSource = null;

    @Bean
    public DataSource dataSource() {
        if (dataSource != null) {
            return dataSource;
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(jdbcUrl);
        this.dataSource = dataSource;
        return this.dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory() {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        return sqlSessionFactory;
    }

    /***
     * 通过@ComponentScan自动扫描，发现MyBatis Mapper接口
     * 也可以通过@MapperScan(value = {"com.aiguibin.business.mapper"})自动扫描,
     * 用@Mapper标注接口，就无需写如下配置类
     * @return Mapper扫描器
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer=new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.aiguibin.business.mapper");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setAnnotationClass(Repository.class);
        return mapperScannerConfigurer;
    }
    @Override
    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}

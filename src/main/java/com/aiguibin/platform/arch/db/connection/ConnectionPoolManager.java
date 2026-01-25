package com.aiguibin.platform.arch.db.connection;

import com.aiguibin.platform.arch.vo.DbConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionPoolManager {
    private Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 根据配置创建连接池
    public synchronized HikariDataSource createDataSource(DbConfig config) {
        // 检查是否已存在对应连接池
        if (dataSources.containsKey(config.getId())) {
            return dataSources.get(config.getId());
        }

        // 创建 HikariDataSource 实例
        HikariConfig hikariConfig = new HikariConfig();

        // 设置数据库连接参数
        String jdbcUrl = getJdbcUrl(config);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword()); // 注意：这里需要解密处理

        // 设置连接池参数
        hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
        hikariConfig.setMinimumIdle(config.getMinIdle());
        hikariConfig.setMaxLifetime(config.getMaxLifetime() > 0 ? config.getMaxLifetime() : 1800000);
        hikariConfig.setConnectionTimeout(config.getConnectionTimeout() > 0 ? config.getConnectionTimeout() : 30000);

        // 设置其他参数
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("DbPool-" + config.getId());

        // 初始化连接池
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        // 存储到 dataSources 映射中
        dataSources.put(config.getId(), dataSource);

        return dataSource;
    }

    // 获取数据库连接
    public Connection getConnection(String configId) throws SQLException {
        // 根据 configId 获取对应数据源
        HikariDataSource dataSource = dataSources.get(configId);
        if (dataSource == null) {
            throw new SQLException("DataSource not found for configId: " + configId);
        }

        // 从数据源获取连接
        return dataSource.getConnection();
    }

    // 释放连接池
    public synchronized void releaseDataSource(String configId) {
        // 根据 configId 获取对应数据源
        HikariDataSource dataSource = dataSources.get(configId);
        if (dataSource != null) {
            // 关闭数据源
            dataSource.close();
            // 从 dataSources 映射中移除
            dataSources.remove(configId);
        }
    }

    // 测试连接池状态
    public boolean testDataSource(String configId) {
        // 根据 configId 获取对应数据源
        HikariDataSource dataSource = dataSources.get(configId);
        if (dataSource == null) {
            return false;
        }

        try {
            // 获取连接并立即关闭
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 根据数据库类型获取 JDBC URL
    private String getJdbcUrl(DbConfig config) {
        String dbType = config.getDbType().toLowerCase();
        String host = config.getHost();
        String port = config.getPort();
        String database = config.getDatabase();

        switch (dbType) {
            case "mysql":
                return "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
            case "oracle":
                return "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
            case "postgresql":
                return "jdbc:postgresql://" + host + ":" + port + "/" + database;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + config.getDbType());
        }
    }

    // 解密密码
    private String decryptPassword(String encryptedPassword) {
        // 这里需要实现密码解密逻辑
        // 注意：实际项目中应使用更安全的加密方式
        return encryptedPassword;
    }
}

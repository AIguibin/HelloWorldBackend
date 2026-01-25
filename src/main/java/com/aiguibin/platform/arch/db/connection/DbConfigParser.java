package com.aiguibin.platform.arch.db.connection;

import com.aiguibin.platform.arch.dto.DbConfig;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbConfigParser {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 解析 Excel 配置文件
    public List<DbConfig> parseExcel(File file) {
        List<DbConfig> configs = new ArrayList<>();
        
        EasyExcel.read(file, DbConfig.class, new AnalysisEventListener<DbConfig>() {
            @Override
            public void invoke(DbConfig data, AnalysisContext context) {
                // 对密码进行加密处理
                if (data.getPassword() != null && !data.getPassword().isEmpty()) {
                    data.setPassword(passwordEncoder.encode(data.getPassword()));
                }
                configs.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 解析完成后的处理
            }
        }).sheet().doRead();
        
        return configs;
    }

    // 解析 YAML 配置文件
    public List<DbConfig> parseYaml(File file) {
        List<DbConfig> configs = new ArrayList<>();
        
        try {
            // 读取 YAML 文件内容
            StringBuilder yamlContentBuilder = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                String line;
                while ((line = reader.readLine()) != null) {
                    yamlContentBuilder.append(line).append(System.lineSeparator());
                }
            }
            String yamlContent = yamlContentBuilder.toString();
            
            // 简单的 YAML 解析逻辑
            // 这里可以使用更专业的 YAML 解析库，如 SnakeYAML
            // 为了简化示例，这里只做基本解析
            Map<String, Object> yamlMap = parseYamlContent(yamlContent);
            List<Map<String, Object>> dbConfigs = (List<Map<String, Object>>) yamlMap.get("db-configs");
            
            if (dbConfigs != null) {
                for (Map<String, Object> dbConfigMap : dbConfigs) {
                    DbConfig config = new DbConfig();
                    config.setId((String) dbConfigMap.get("id"));
                    config.setName((String) dbConfigMap.get("name"));
                    config.setDbType((String) dbConfigMap.get("db-type"));
                    config.setHost((String) dbConfigMap.get("host"));
                    config.setPort((String) dbConfigMap.get("port"));
                    config.setDatabase((String) dbConfigMap.get("database"));
                    config.setUsername((String) dbConfigMap.get("username"));
                    
                    // 对密码进行加密处理
                    String password = (String) dbConfigMap.get("password");
                    if (password != null && !password.isEmpty()) {
                        config.setPassword(passwordEncoder.encode(password));
                    }
                    
                    // 连接池参数
                    config.setMaxPoolSize((int) dbConfigMap.getOrDefault("max-pool-size", 10));
                    config.setMinIdle((int) dbConfigMap.getOrDefault("min-idle", 5));
                    config.setEnabled((boolean) dbConfigMap.getOrDefault("enabled", true));
                    
                    configs.add(config);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return configs;
    }

    // 简单的 YAML 解析方法（实际项目中建议使用专业库）
    private Map<String, Object> parseYamlContent(String content) {
        // 这里只是示例，实际项目中应使用 SnakeYAML 等库
        return new java.util.HashMap<>();
    }

    // 测试数据库连接
    public boolean testConnection(DbConfig config) {
        ConnectionPoolManager poolManager = new ConnectionPoolManager();
        try {
            // 创建临时数据源
            poolManager.createDataSource(config);
            // 获取连接
            poolManager.getConnection(config.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // 释放数据源
            poolManager.releaseDataSource(config.getId());
        }
    }
}

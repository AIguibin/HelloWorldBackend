package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.db.connection.DbConfigParser;
import com.aiguibin.platform.arch.db.connection.ConnectionPoolManager;
import com.aiguibin.platform.arch.service.DbConfigService;
import com.aiguibin.platform.arch.vo.DbConfig;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DbConfigServiceImpl implements DbConfigService {
    private Map<String, DbConfig> dbConfigs = new HashMap<>();
    private ConnectionPoolManager poolManager = new ConnectionPoolManager();

    @Override
    public List<DbConfig> getDbConfigs() {
        return new ArrayList<>(dbConfigs.values());
    }

    @Override
    public DbConfig getDbConfig(String id) {
        return dbConfigs.get(id);
    }

    @Override
    public String createDbConfig(DbConfig config) {
        String id = UUID.randomUUID().toString();
        config.setId(id);
        dbConfigs.put(id, config);
        return id;
    }

    @Override
    public void updateDbConfig(String id, DbConfig config) {
        if (dbConfigs.containsKey(id)) {
            config.setId(id);
            dbConfigs.put(id, config);
        }
    }

    @Override
    public void deleteDbConfig(String id) {
        dbConfigs.remove(id);
        poolManager.releaseDataSource(id);
    }

    @Override
    public boolean testConnection(DbConfig config) {
        DbConfigParser parser = new DbConfigParser();
        return parser.testConnection(config);
    }

    /**
     * 从文件加载数据库连接配置
     * @param file 配置文件
     */
    public void loadDbConfigsFromFile(File file) {
        DbConfigParser parser = new DbConfigParser();
        List<DbConfig> configs = parser.parseExcel(file);
        for (DbConfig config : configs) {
            String id = UUID.randomUUID().toString();
            config.setId(id);
            dbConfigs.put(id, config);
        }
    }
}

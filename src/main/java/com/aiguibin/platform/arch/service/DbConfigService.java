package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.vo.DbConfig;

import java.util.List;

public interface DbConfigService {
    /**
     * 获取数据库连接配置列表
     * @return 数据库连接配置列表
     */
    List<DbConfig> getDbConfigs();

    /**
     * 获取数据库连接配置详情
     * @param id 配置ID
     * @return 数据库连接配置详情
     */
    DbConfig getDbConfig(String id);

    /**
     * 创建数据库连接配置
     * @param config 数据库连接配置
     * @return 配置ID
     */
    String createDbConfig(DbConfig config);

    /**
     * 更新数据库连接配置
     * @param id 配置ID
     * @param config 数据库连接配置
     */
    void updateDbConfig(String id, DbConfig config);

    /**
     * 删除数据库连接配置
     * @param id 配置ID
     */
    void deleteDbConfig(String id);

    /**
     * 测试数据库连接
     * @param config 数据库连接配置
     * @return 测试结果
     */
    boolean testConnection(DbConfig config);
}

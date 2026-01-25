package com.aiguibin.platform.arch.vo;

import lombok.Data;

@Data
public class DbConfig {
    private String id;              // 连接配置ID
    private String name;            // 连接名称
    private String dbType;          // 数据库类型
    private String host;            // 主机地址
    private String port;            // 端口
    private String database;        // 数据库名称
    private String username;        // 用户名
    private String password;        // 密码（加密存储）
    private int maxPoolSize;        // 最大连接数
    private int minIdle;            // 最小空闲连接数
    private long maxLifetime;       // 连接最大生命周期
    private long connectionTimeout; // 连接超时时间
    private boolean enabled;        // 是否启用
}

package com.aiguibin.platform.arch.dto;

import lombok.Data;

import java.util.List;

/**
 * 表结构校验请求对象
 */
@Data
public class ValidationRequest {
    private String dbConfigId; // 数据库连接配置ID
    private String documentPath; // 表结构文档路径
    private List<String> tables; // 表名列表，为空则校验所有表
}

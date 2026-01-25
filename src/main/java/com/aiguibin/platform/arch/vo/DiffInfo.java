package com.aiguibin.platform.arch.vo;

import lombok.Data;

@Data
public class DiffInfo {
    private String diffType;        // 差异类型
    private String tableName;       // 表名
    private String columnName;      // 字段名（可选）
    private String docValue;        // 文档中的值
    private String dbValue;         // 数据库中的值
    private String description;     // 差异描述
    private String fixScript;       // 修复脚本
}

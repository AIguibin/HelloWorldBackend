package com.aiguibin.platform.arch.vo;

import lombok.Data;

@Data
public class ColumnStructure {
    private String columnName;    // 字段名
    private String columnType;    // 字段类型
    private int columnLength;     // 字段长度
    private String columnComment; // 字段注释
    private boolean nullable;     // 是否可为空
    private String defaultValue;  // 默认值
}

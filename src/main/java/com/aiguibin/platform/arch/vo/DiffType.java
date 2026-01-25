package com.aiguibin.platform.arch.vo;

public enum DiffType {
    TABLE_MISSING,        // 表缺失
    TABLE_EXTRA,          // 表多余
    COLUMN_MISSING,       // 字段缺失
    COLUMN_EXTRA,         // 字段多余
    COLUMN_TYPE_MISMATCH, // 字段类型不匹配
    COLUMN_LENGTH_MISMATCH, // 字段长度不匹配
    COLUMN_COMMENT_MISMATCH, // 字段注释不匹配
    INDEX_MISSING,        // 索引缺失
    INDEX_EXTRA,          // 索引多余
    CONSTRAINT_MISSING,   // 约束缺失
    CONSTRAINT_EXTRA      // 约束多余
}

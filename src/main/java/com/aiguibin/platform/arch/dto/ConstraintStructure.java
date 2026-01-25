package com.aiguibin.platform.arch.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConstraintStructure {
    private String constraintName; // 约束名
    private String constraintType; // 约束类型
    private List<String> columns;  // 约束列
    private String referencedTable; // 引用表（外键约束）
    private List<String> referencedColumns; // 引用列（外键约束）
}

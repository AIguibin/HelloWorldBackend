package com.aiguibin.platform.arch.vo;

import lombok.Data;
import java.util.List;

@Data
public class TableStructure {
    private String tableName;               // 表名
    private String tableComment;            // 表注释
    private List<ColumnStructure> columns;  // 字段列表
    private List<IndexStructure> indexes;   // 索引列表
    private List<ConstraintStructure> constraints; // 约束列表
}

package com.aiguibin.platform.arch.vo;

import lombok.Data;
import java.util.List;

@Data
public class IndexStructure {
    private String indexName;     // 索引名
    private List<String> columns; // 索引列
    private boolean unique;       // 是否唯一索引
}

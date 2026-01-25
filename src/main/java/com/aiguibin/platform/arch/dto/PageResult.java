package com.aiguibin.platform.arch.dto;

import lombok.Data;
import java.util.List;

/**
 * 分页结果视图对象
 */
@Data
public class PageResult<T> {
    
    /**
     * 记录列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页码
     */
    private long current;
    
    /**
     * 每页大小
     */
    private long size;
    
    /**
     * 总页数
     */
    private long pages;
}
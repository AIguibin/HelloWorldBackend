package com.aiguibin.platform.arch.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResultVO<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private Integer totalPages;
    private List<T> list;
    
    public PageResultVO(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}
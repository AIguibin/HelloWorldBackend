package com.aiguibin.platform.arch.dto;

import lombok.Data;

/**
 * 字典类型VO
 */
@Data
public class DictTypeVO {
    /**
     * 字典类型UUID
     */
    private String uuid;
    
    /**
     * 字典类型编码
     */
    private String dctTp;
    
    /**
     * 字典类型名称
     */
    private String dctTpNm;
}
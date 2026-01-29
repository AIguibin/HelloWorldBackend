package com.aiguibin.platform.arch.dto;

import lombok.Data;

import java.util.List;

/**
 * 字典类型详情VO
 */
@Data
public class DictTypeDetailVO {
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
    
    /**
     * 字典类型描述
     */
    private String dctTpDesc;
    
    /**
     * 字典项列表
     */
    private List<DictItemVO> dictItems;
    
    /**
     * 字典项VO
     */
    @Data
    public static class DictItemVO {
        /**
         * 字典项UUID
         */
        private String uuid;
        
        /**
         * 字典项排序
         */
        private Integer dctSeq;
        
        /**
         * 字典项键
         */
        private String dctKey;
        
        /**
         * 字典项值
         */
        private String dctVal;
        
        /**
         * 字典项描述
         */
        private String dctDesc;
        
        /**
         * 状态
         */
        private String status;
    }
}

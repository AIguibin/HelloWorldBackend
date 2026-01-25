package com.aiguibin.platform.arch.dto;

import lombok.Data;

/**
 * 字典项导入VO
 */
@Data
public class DictItemImportVO {

    /**
     * 行号
     */
    private int rowNum;

    /**
     * 字典键
     */
    private String dctKey;

    /**
     * 字典值名称
     */
    private String dctValNm;

    /**
     * 字典值
     */
    private String dctVal;

    /**
     * 字典描述
     */
    private String dctDsc;

    /**
     * 排序号
     */
    private Integer dctSeq;

    /**
     * 分组编码
     */
    private String dctGrp;

    /**
     * 状态码
     */
    private String stcd;

    /**
     * 导入状态
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;
}

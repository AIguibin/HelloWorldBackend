package com.aiguibin.online.table.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型表
 */
@Data
@TableName("dict_type")
public class DictType {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 字典类型编码
     */
    private String dictTypeCode;
    
    /**
     * 字典类型名称
     */
    private String dictTypeName;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否启用 0-禁用 1-启用
     */
    private Integer isEnabled;
    
    /**
     * 创建人
     */
    private String createUser;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新人
     */
    private String updateUser;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
}
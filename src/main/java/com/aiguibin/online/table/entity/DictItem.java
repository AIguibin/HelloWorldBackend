package com.aiguibin.online.table.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典码值表
 */
@Data
@TableName("dict_item")
public class DictItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 字典类型ID
     */
    private Long dictTypeId;
    
    /**
     * 字典类型编码（冗余字段，便于查询）
     */
    private String dictTypeCode;
    
    /**
     * 字典码值
     */
    private String dictValue;
    
    /**
     * 字典名称
     */
    private String dictLabel;
    
    /**
     * 分组编码
     * 用于同一个字典类型下的码值分组，不同分组可以有不同的组合
     */
    private String groupCode;
    
    /**
     * 分组名称
     */
    private String groupName;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否启用 0-禁用 1-启用
     */
    private Integer isEnabled;
    
    /**
     * 描述
     */
    private String description;
    
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
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典码值表
 */
@Data
@TableName("sys_dict_item")
public class DictItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 字典类型编码
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
    private Integer sortOrder;
    
    /**
     * 状态：1-启用 0-禁用
     */
    private Integer status;
    

    
    /**
     * 创建人
     */
    @TableField("created_by")
    private String createUser;
    
    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createTime;
    
    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updateUser;
    
    /**
     * 更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updateTime;
    
    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
}
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典码值表实体类
 * 对应sys_dict_item表，用于存储字典码值信息
 */
@Data
@TableName("sys_dict_item")
public class DictItem {
    /**
     * UUID，32位随机字符串
     */
    private String uuid;
    
    /**
     * 自增ID，仅做序号
     */
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
     * 排序号
     */
    private Integer sortOrder;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建人用户编号
     */
    @TableField("created_by")
    private String createUser;
    
    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createTime;
    
    /**
     * 更新人用户编号
     */
    @TableField("updated_by")
    private String updateUser;
    
    /**
     * 更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updateTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}
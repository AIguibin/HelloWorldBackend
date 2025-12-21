package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型表实体类
 * 对应sys_dict_type表，用于存储字典类型信息
 */
@Data
@TableName("sys_dict_type")
public class DictType {
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
     * 字典类型名称
     */
    private String dictTypeName;
    
    /**
     * 描述
     */
    private String description;
    
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
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务类型表实体类
 * 对应biz_business_type表，用于存储业务类型信息
 */
@Data
@TableName("biz_business_type")
public class BusinessType {
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
     * 业务类型编码，如CHANGE_RECORD
     */
    private String typeCode;
    
    /**
     * 业务类型名称，如变更记录
     */
    private String typeName;
    
    /**
     * 主表名，如biz_change_record
     */
    private String mainTableName;
    
    /**
     * 主键字段名
     */
    private String idFieldName;
    
    /**
     * 编码字段名，如record_code
     */
    private String codeFieldName;
    
    /**
     * 状态字段名，用于待办显示
     */
    private String statusFieldName;
    
    /**
     * 标题字段名，用于待办显示
     */
    private String titleFieldName;
    
    /**
     * 是否激活：0-否，1-是
     */
    private Integer isActive;
    
    /**
     * 业务类型描述
     */
    private String description;

    /**
     * 创建人用户编号
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新人用户编号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}
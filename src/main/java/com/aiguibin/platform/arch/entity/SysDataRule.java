package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据规则表实体类
 * 对应sys_data_rule表，用于存储数据权限规则
 */
@Data
@TableName("sys_data_rule")
public class SysDataRule {
    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则编码，10位
     */
    private String ruleCode;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 规则类型：1-预定义，2-自定义SQL，3-组合规则
     */
    private Integer ruleType;
    
    /**
     * 关联的权限编码
     */
    private String permCode;
    
    /**
     * 业务实体类型（如：sys_user, sys_order）
     */
    private String entityType;
    
    /**
     * 预定义范围：1-全部，2-本机构，3-本部门，4-本人
     */
    private Integer scopeType;
    
    /**
     * 是否包含下级：0-否，1-是（针对机构、部门）
     */
    private Integer includeChildren;
    
    /**
     * 自定义SQL条件（WHERE子句内容）
     */
    private String customSql;
    
    /**
     * 组合规则表达式（JSON格式）
     */
    @JsonProperty("rule_expression")
    private String ruleExpression;
    
    /**
     * 规则优先级：1-高，2-中，3-低
     */
    private Integer rulePriority;
    
    /**
     * 是否全局规则：0-否，1-是
     */
    private Integer isGlobal;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 规则描述
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
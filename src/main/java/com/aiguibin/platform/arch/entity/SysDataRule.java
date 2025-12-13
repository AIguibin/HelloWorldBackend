package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_data_rule")
public class SysDataRule {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleCode; // 规则编码，10位
    private String ruleName; // 规则名称
    private Integer ruleType; // 规则类型：1-预定义，2-自定义SQL，3-组合规则
    private String permCode; // 关联的权限编码
    private String entityType; // 业务实体类型（如：sys_user, sys_order）
    private Integer scopeType; // 预定义范围：1-全部，2-本机构，3-本部门，4-本人
    private Integer includeChildren; // 是否包含下级：0-否，1-是（针对机构、部门）
    private String customSql; // 自定义SQL条件（WHERE子句内容）
    @JsonProperty("rule_expression")
    private String ruleExpression; // 组合规则表达式（JSON格式）
    private Integer rulePriority; // 规则优先级：1-高，2-中，3-低
    private Integer isGlobal; // 是否全局规则：0-否，1-是
    private Integer status; // 状态：0-禁用，1-启用
    private String description; // 规则描述

    @TableField(fill = FieldFill.INSERT)
    private String createdBy; // 创建人用户编号
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy; // 更新人用户编号
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    private Integer isDeleted; // 是否删除：0-否，1-是
}
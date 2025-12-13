package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_business_type")
public class BusinessType {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String typeCode; // 业务类型编码，如CHANGE_RECORD
    private String typeName; // 业务类型名称，如变更记录
    private String mainTableName; // 主表名，如biz_change_record
    private String idFieldName; // 主键字段名
    private String codeFieldName; // 编码字段名，如record_code
    private String statusFieldName; // 状态字段名，用于待办显示
    private String titleFieldName; // 标题字段名，用于待办显示
    private Integer isActive; // 是否激活：0-否，1-是
    private String description; // 业务类型描述

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
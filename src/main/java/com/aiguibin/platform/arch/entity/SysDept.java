package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dept")
public class SysDept {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String deptCode; // 部门编码，15位：机构编码(10位)+D+4位序列
    private String deptName; // 部门名称
    private String orgCode; // 所属机构编码
    private String managerNum; // 部门负责人用户编号
    private Integer sortOrder; // 排序号
    private Integer status; // 状态：0-禁用，1-启用
    private String description; // 部门描述

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
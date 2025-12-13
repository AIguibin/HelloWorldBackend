package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_time_permission")
public class SysTimePermission {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String permCode; // 权限编码
    private String startTime; // 开始时间（格式：HH:mm:ss）
    private String endTime; // 结束时间（格式：HH:mm:ss）
    private Integer weekDays; // 允许的星期几，位图：1-周一，2-周二，...，64-周日，多个相加
    private Integer status; // 状态：0-禁用，1-启用
    private String description; // 时间权限描述

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
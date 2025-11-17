package com.aiguibin.online.table.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleCode; // 角色编码
    private String roleName; // 角色名称
    private Integer roleType; // 角色类型 1:系统角色 2:业务角色
    private Integer dataScopeType; // 数据权限范围 1:全部 2:本机构 3:本部门 4:本人 5:自定义
    private String description; // 描述
    private Integer status; // 状态 0:停用 1:启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}


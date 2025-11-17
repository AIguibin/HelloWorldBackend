package com.aiguibin.online.table.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class Menu {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId; // 父菜单ID
    private Integer menuType; // 菜单类型 1:目录 2:菜单 3:按钮 4:接口
    private String menuName; // 菜单名称
    private String menuCode; // 菜单编码
    private String path; // 路由路径
    private String component; // 组件路径
    private String icon; // 图标
    private String perms; // 权限标识
    private Integer sortOrder; // 排序号
    private Integer isVisible; // 是否显示 0:隐藏 1:显示
    private Integer status; // 状态 0:停用 1:启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段：子菜单列表
    @TableField(exist = false)
    private List<Menu> children;
}


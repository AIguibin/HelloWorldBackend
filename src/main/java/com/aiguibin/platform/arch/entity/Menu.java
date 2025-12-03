package com.aiguibin.platform.arch.entity;

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

    private String menuCode; // 菜单编码，20位
    private String menuName; // 菜单名称
    private Integer menuType; // 类型：1-目录，2-页面，3-按钮
    private String parentMenuCode; // 父菜单编码
    private String icon; // 图标
    private String path; // 路由路径（前端使用）
    private String component; // 组件路径（前端使用）
    private String url; // 访问URL（后端API路径）
    private String httpMethod; // HTTP方法：GET,POST,PUT,DELETE等
    private Integer isExternal; // 是否外部链接：0-否，1-是
    private Integer isCache; // 是否缓存：0-否，1-是
    private Integer isVisible; // 是否显示：0-否，1-是
    private String permissionKey; // 权限标识（如：user:view）
    private Integer sortOrder; // 排序号
    private Integer status; // 状态：0-禁用，1-启用
    private String description; // 菜单描述
    private String createdBy; // 创建人用户编号
    private LocalDateTime createdTime; // 创建时间
    private String updatedBy; // 更新人用户编号
    private LocalDateTime updatedTime; // 更新时间
    private Integer isDeleted; // 是否删除：0-否，1-是

    // 非数据库字段：子菜单列表
    @TableField(exist = false)
    private List<Menu> children;
}


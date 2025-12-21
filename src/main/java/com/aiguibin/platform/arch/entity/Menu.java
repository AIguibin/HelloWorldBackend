package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单表实体类
 * 对应sys_menu表，用于存储菜单信息
 */
@Data
@TableName("sys_menu")
public class Menu {
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
     * 菜单编码，20位
     */
    private String menuCode;
    
    /**
     * 菜单名称
     */
    private String menuName;
    
    /**
     * 类型：M-目录，P-页面，C-组件，B-按钮
     */
    private String menuType;
    
    /**
     * 父菜单编码
     */
    private String parentMenuCode;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 前端路由路径
     */
    private String path;
    
    /**
     * 前端组件路径
     */
    private String component;
    
    /**
     * 资源标识（如：system:user:list）
     */
    private String resourceKey;
    
    /**
     * 资源类型：MENU-菜单，BUTTON-按钮，PAGE-页面
     */
    private String resourceType;
    
    /**
     * 是否外部链接：0-否，1-是
     */
    private Integer isExternal;
    
    /**
     * 是否缓存：0-否，1-是
     */
    private Integer isCache;
    
    /**
     * 是否显示：0-否，1-是
     */
    private Integer isVisible;
    
    /**
     * 排序号
     */
    private Integer sortOrder;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 菜单描述
     */
    private String description;
    
    /**
     * 创建人用户编号
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新人用户编号
     */
    private String updatedBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;

    /**
     * 非数据库字段：子菜单列表
     */
    @TableField(exist = false)
    private List<Menu> children;
}


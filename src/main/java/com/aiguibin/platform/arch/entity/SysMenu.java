package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_menu")
public class SysMenu {

    @TableField("uuid")
    private String uuid;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("menu_code")
    private String menuCode;

    @TableField("menu_name")
    private String menuName;

    @TableField("menu_type")
    private String menuType;

    @TableField("parent_menu_code")
    private String parentMenuCode;

    @TableField("icon")
    private String icon;

    @TableField("path")
    private String path;

    @TableField("component")
    private String component;

    @TableField("resource_key")
    private String resourceKey;

    @TableField("resource_type")
    private String resourceType;

    @TableField("is_external")
    private Integer isExternal;

    @TableField("is_cache")
    private Integer isCache;

    @TableField("is_visible")
    private Integer isVisible;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("status")
    private Integer status;

    @TableField("description")
    private String description;

    @TableField("created_by")
    private String createdBy;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createdTime;

    @TableField("updated_by")
    private String updatedBy;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

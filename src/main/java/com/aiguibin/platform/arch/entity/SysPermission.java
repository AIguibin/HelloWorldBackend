package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统权限表实体类
 * 对应sys_permission表，用于存储系统权限信息
 */
@Data
@TableName("sys_permission")
public class SysPermission {

    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限编码，5位：P+4位数字
     */
    private String permCode;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限标识（如：user:create）
     */
    private String permKey;

    /**
     * 权限类型：1-菜单，2-操作，3-接口，4-数据，5-字段，6-时间，7-业务
     */
    private Integer permType;

    /**
     * 关联的菜单编码（当perm_type=1,2时）
     */
    private String menuCode;

    /**
     * 接口路径（当perm_type=3时）
     */
    private String apiPath;

    /**
     * 业务实体类型（如：user,order，当perm_type=4,5时）
     */
    private String entityType;

    /**
     * 业务实体字段（当perm_type=5时）
     */
    private String entityField;

    /**
     * 规则类型：1-预定义，2-自定义SQL
     */
    private Integer ruleType;

    /**
     * 预定义范围：1-全部，2-本机构，3-本部门，4-本人
     */
    private Integer scopeType;

    /**
     * 自定义规则（JSON或SQL片段）
     */
    private String customRule;

    /**
     * 是否默认权限：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 权限描述
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
}
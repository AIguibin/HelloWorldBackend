package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API资源定义表实体类
 * 对应sys_api_resource表，用于存储API资源定义信息
 */
@Data
@TableName("sys_api_resource")
public class SysApiResource {
    /**
     * UUID，32位随机字符串
     */
    private String uuid;

    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * API编码，API+29位数字
     */
    private String apiCode;

    /**
     * API名称
     */
    private String apiName;

    /**
     * API路径（支持Ant风格）
     */
    private String apiPath;

    /**
     * HTTP方法
     */
    private String httpMethod;

    /**
     * 资源标识
     */
    private String resourceKey;

    /**
     * 所属服务名称
     */
    private String serviceName;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 限流次数/秒
     */
    private Integer rateLimit;

    /**
     * 是否需要认证：0-否，1-是
     */
    private Integer needAuth;

    /**
     * 是否记录日志：0-否，1-是
     */
    private Integer logEnabled;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * API描述
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
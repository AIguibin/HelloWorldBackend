package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 信贷系统外部链接网络管理清单实体类
 * 对应biz_network_link表
 */
@Data
@TableName("biz_network_link")
public class NetExternalLink {
    
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
     * 链路编号，唯一
     */
    @NotBlank(message = "链路编号不能为空")
    @Size(max = 64, message = "链路编号长度不能超过64个字符")
    private String linkCode;
    
    /**
     * 链路名称
     */
    @NotBlank(message = "链路名称不能为空")
    @Size(max = 256, message = "链路名称长度不能超过256个字符")
    private String linkName;
    
    /**
     * 源系统名称
     */
    @NotBlank(message = "源系统名称不能为空")
    @Size(max = 128, message = "源系统名称长度不能超过128个字符")
    private String sourceSystem;
    
    /**
     * 源环境：prod-生产，uat-UAT测试，sit-SIT测试，dev-开发环境
     */
    @NotBlank(message = "源环境不能为空")
    @Size(max = 16, message = "源环境长度不能超过16个字符")
    private String sourceEnv;
    
    /**
     * 源IP地址
     */
    @NotBlank(message = "源IP地址不能为空")
    @Size(max = 64, message = "源IP地址长度不能超过64个字符")
    private String sourceIp;
    
    /**
     * 目标系统名称
     */
    @NotBlank(message = "目标系统名称不能为空")
    @Size(max = 128, message = "目标系统名称长度不能超过128个字符")
    private String targetSystem;
    
    /**
     * 目标环境：prod-生产，uat-UAT测试，sit-SIT测试，dev-开发环境
     */
    @NotBlank(message = "目标环境不能为空")
    @Size(max = 16, message = "目标环境长度不能超过16个字符")
    private String targetEnv;
    
    /**
     * 目标主机（IP地址或域名）
     */
    @NotBlank(message = "目标主机不能为空")
    @Size(max = 255, message = "目标主机长度不能超过255个字符")
    private String targetHost;
    
    /**
     * 目标端口号（1-65535）
     */
    @NotNull(message = "目标端口号不能为空")
    @Min(value = 1, message = "目标端口号不能小于1")
    @Max(value = 65535, message = "目标端口号不能大于65535")
    private Integer targetPort;
    
    /**
     * 连接协议：HTTP/HTTPS/TCP/SFTP/MQ等
     */
    @NotBlank(message = "连接协议不能为空")
    @Size(max = 32, message = "连接协议长度不能超过32个字符")
    private String protocol;
    
    /**
     * 认证方式
     */
    @Size(max = 512, message = "认证方式长度不能超过512个字符")
    private String authMethod;
    
    /**
     * 使用场景
     */
    @Size(max = 512, message = "使用场景长度不能超过512个字符")
    private String scenario;
    
    /**
     * 链路描述
     */
    @Size(max = 1024, message = "链路描述长度不能超过1024个字符")
    private String description;
    
    /**
     * 负责人
     */
    @Size(max = 64, message = "负责人长度不能超过64个字符")
    private String owner;
    
    /**
     * 链路状态：active-启用中，testing-测试中，disabled-已停用
     */
    @NotBlank(message = "链路状态不能为空")
    @Size(max = 16, message = "链路状态长度不能超过16个字符")
    private String status;
    
    /**
     * 监控等级：P0-核心，P1-重要，P2-一般
     */
    @Size(max = 32, message = "监控等级长度不能超过32个字符")
    private String monitoringLevel;
    
    /**
     * 创建人用户编号
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新人用户编号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}

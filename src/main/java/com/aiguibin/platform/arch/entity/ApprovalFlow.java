package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审批流程表实体类
 * 对应biz_approval_flow表，用于存储审批流程定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_approval_flow")
public class ApprovalFlow {
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
     * 流程ID，如FLOW_CHG_001
     */
    private String flowId;
    
    /**
     * 流程名称
     */
    private String flowName;
    
    /**
     * 业务类型：CHANGE_RECORD-变更记录，RELEASE-发版记录，DB_CHANGE-数据库变更，CONFIG_CHANGE-配置变更，RESOURCE_APPLY-资源申请
     */
    private String businessType;
    
    /**
     * 流程描述
     */
    private String description;
    
    /**
     * 是否激活：0-否，1-是
     */
    private Integer isActive;
    
    /**
     * 是否默认流程：0-否，1-是
     */
    private Integer isDefault;
    
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
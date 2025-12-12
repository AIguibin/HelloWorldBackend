package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_approval_flow")
public class ApprovalFlow {
    @TableId(type = IdType.AUTO)
    private Long id; // 自增ID，仅做序号
    private String flowId; // 流程ID，如FLOW_CHG_001
    private String flowName; // 流程名称
    private String businessType; // 业务类型：CHANGE_RECORD-变更记录，RELEASE-发版记录，DB_CHANGE-数据库变更，CONFIG_CHANGE-配置变更，RESOURCE_APPLY-资源申请
    private String description; // 流程描述
    private Integer isActive; // 是否激活：0-否，1-是
    private Integer isDefault; // 是否默认流程：0-否，1-是
    private String createdBy; // 创建人用户编号
    private LocalDateTime createdTime; // 创建时间
    private String updatedBy; // 更新人用户编号
    private LocalDateTime updatedTime; // 更新时间
    private Integer isDeleted; // 是否删除：0-否，1-是
}
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
 * 审批任务表实体类
 * 对应biz_approval_task表，用于存储审批任务信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_approval_task")
public class ApprovalTask {
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
     * 任务ID，如TASK_20241212_0001
     */
    private String taskId;
    
    /**
     * 所属流程ID
     */
    private String flowId;
    
    /**
     * 所属节点ID
     */
    private String nodeId;
    
    /**
     * 业务类型
     */
    private String businessType;
    
    /**
     * 业务数据ID
     */
    private Long businessId;
    
    /**
     * 业务数据编码
     */
    private String businessCode;
    
    /**
     * 审批人用户编号
     */
    private String approverNum;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 任务状态：PENDING-待审批，APPROVED-已通过，REJECTED-已拒绝，CANCELED-已取消
     */
    private String taskStatus;
    
    /**
     * 当前业务状态
     */
    private String currentStatus;
    
    /**
     * 分配时间
     */
    private LocalDateTime assignTime;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 审批备注
     */
    private String approvalRemark;
    
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
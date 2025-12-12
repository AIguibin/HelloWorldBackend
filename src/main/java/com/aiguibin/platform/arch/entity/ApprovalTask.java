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
@TableName("biz_approval_task")
public class ApprovalTask {
    @TableId(type = IdType.AUTO)
    private Long id; // 自增ID，仅做序号
    private String taskId; // 任务ID，如TASK_20241212_0001
    private String flowId; // 所属流程ID
    private String nodeId; // 所属节点ID
    private String businessType; // 业务类型
    private Long businessId; // 业务数据ID
    private String businessCode; // 业务数据编码
    private String approverNum; // 审批人用户编号
    private String approverName; // 审批人姓名
    private String taskStatus; // 任务状态：PENDING-待审批，APPROVED-已通过，REJECTED-已拒绝，CANCELED-已取消
    private String currentStatus; // 当前业务状态
    private LocalDateTime assignTime; // 分配时间
    private LocalDateTime approvalTime; // 审批时间
    private String approvalRemark; // 审批备注
    private String createdBy; // 创建人用户编号
    private LocalDateTime createdTime; // 创建时间
    private String updatedBy; // 更新人用户编号
    private LocalDateTime updatedTime; // 更新时间
    private Integer isDeleted; // 是否删除：0-否，1-是
}
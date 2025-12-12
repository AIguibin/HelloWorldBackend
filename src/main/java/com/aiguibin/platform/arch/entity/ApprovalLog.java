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
@TableName("biz_approval_log")
public class ApprovalLog {
    @TableId(type = IdType.AUTO)
    private Long id; // 自增ID，仅做序号
    private String logId; // 日志ID，如LOG_20241212_0001
    private String taskId; // 关联任务ID
    private String flowId; // 所属流程ID
    private String nodeId; // 所属节点ID
    private String businessType; // 业务类型
    private Long businessId; // 业务数据ID
    private String businessCode; // 业务数据编码
    private String operationType; // 操作类型：SUBMIT-提交，APPROVE-通过，REJECT-拒绝，TRANSFER-转办，CANCEL-取消
    private String operatorNum; // 操作人用户编号
    private String operatorName; // 操作人姓名
    private LocalDateTime operationTime; // 操作时间
    private String operationRemark; // 操作备注
    private String beforeStatus; // 操作前状态
    private String afterStatus; // 操作后状态
    private String createdBy; // 创建人用户编号
    private LocalDateTime createdTime; // 创建时间
    private Integer isDeleted; // 是否删除：0-否，1-是
}
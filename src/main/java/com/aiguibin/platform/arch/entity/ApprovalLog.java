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
 * 审批日志表实体类
 * 对应biz_approval_log表，用于存储审批过程的操作日志
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_approval_log")
public class ApprovalLog {
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
     * 日志ID，如LOG_20241212_0001
     */
    private String logId;
    
    /**
     * 关联任务ID
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
     * 操作类型：SUBMIT-提交，APPROVE-通过，REJECT-拒绝，TRANSFER-转办，CANCEL-取消
     */
    private String operationType;
    
    /**
     * 操作人用户编号
     */
    private String operatorNum;
    
    /**
     * 操作人姓名
     */
    private String operatorName;
    
    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
    
    /**
     * 操作备注
     */
    private String operationRemark;
    
    /**
     * 操作前状态
     */
    private String beforeStatus;
    
    /**
     * 操作后状态
     */
    private String afterStatus;
    
    /**
     * 创建人用户编号
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}
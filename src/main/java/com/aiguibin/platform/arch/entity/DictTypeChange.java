package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.aiguibin.platform.arch.enums.ChangeType;
import com.aiguibin.platform.arch.enums.ApproveStatus;
import com.aiguibin.platform.arch.enums.ExecuteStatus;
import lombok.Data;
import java.util.Date;

/**
 * 字典类型变更主表实体
 */
@Data
@TableName("biz_ddct_type_change")
public class DictTypeChange {
    
    /**
     * 业务主键（表字段：uuid）
     */
    @TableId(value = "uuid", type = IdType.INPUT)
    private String uuid;
    
    /**
     * 自增序号（表字段：id）
     */
    @TableField("id")
    private Long id;
    
    @TableField("change_no")
    private String changeNo;
    
    @TableField("dct_tp_id")
    private String dctTpId;
    
    @TableField("change_type")
    private String changeType;
    
    @TableField("old_dct_tp")
    private String oldDctTp;
    
    @TableField("new_dct_tp")
    private String newDctTp;
    
    @TableField("old_dct_tp_nm")
    private String oldDctTpNm;
    
    @TableField("new_dct_tp_nm")
    private String newDctTpNm;
    
    @TableField("change_reason")
    private String changeReason;
    
    @TableField("change_impact")
    private String changeImpact;
    
    @TableField("apply_user_num")
    private String applyUserNum;
    
    @TableField("apply_user_name")
    private String applyUserName;
    
    @TableField("apply_time")
    private Date applyTime;
    
    @TableField("approve_status")
    private String approveStatus;
    
    @TableField("execute_status")
    private String executeStatus;
    
    @TableField("approver_num")
    private String approverNum;
    
    @TableField("approver_name")
    private String approverName;
    
    @TableField("approve_time")
    private Date approveTime;
    
    @TableField("approve_remark")
    private String approveRemark;
    
    @TableField("execute_user_num")
    private String executeUserNum;
    
    @TableField("execute_user_name")
    private String executeUserName;
    
    @TableField("execute_time")
    private Date executeTime;
    
    @TableField("execute_result")
    private String executeResult;
    
    @TableField("flow_id")
    private String flowId;
    
    @TableField("current_node_id")
    private String currentNodeId;
    
    @TableField("approval_instance_id")
    private String approvalInstanceId;
    
    @TableField("created_by")
    private String createdBy;
    
    @TableField("created_time")
    private Date createdTime;
    
    @TableField("updated_by")
    private String updatedBy;
    
    @TableField("updated_time")
    private Date updatedTime;
    
    @TableField("is_deleted")
    private Integer isDeleted;
    
    @TableField(exist = false)
    private String changeTypeName;
    
    @TableField(exist = false)
    private String approveStatusName;
    
    @TableField(exist = false)
    private String executeStatusName;
    
    @TableField(exist = false)
    private java.util.List<DictItemChange> itemChanges;
    
    /**
     * 获取变更类型名称
     */
    public String getChangeTypeName() {
        if (changeType != null) {
            ChangeType type = ChangeType.getByCode(changeType);
            if (type != null) {
                return type.getName();
            }
        }
        return changeType;
    }
    
    /**
     * 获取审批状态名称
     */
    public String getApproveStatusName() {
        if (approveStatus != null) {
            ApproveStatus status = ApproveStatus.getByCode(approveStatus);
            if (status != null) {
                return status.getName();
            }
        }
        return approveStatus;
    }
    
    /**
     * 获取执行状态名称
     */
    public String getExecuteStatusName() {
        if (executeStatus != null) {
            ExecuteStatus status = ExecuteStatus.getByCode(executeStatus);
            if (status != null) {
                return status.getName();
            }
        }
        return executeStatus;
    }
}

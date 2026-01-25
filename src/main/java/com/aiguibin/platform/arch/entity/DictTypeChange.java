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
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String changeNo;
    
    private String dctTpId;
    
    private String changeType;
    
    private String oldDctTp;
    
    private String newDctTp;
    
    private String oldDctTpNm;
    
    private String newDctTpNm;
    
    private String changeReason;
    
    private String changeImpact;
    
    private String applyUser;
    
    private Date applyTime;
    
    private String approveStatus;
    
    private String executeStatus;
    
    private String approveUser;
    
    private Date approveTime;
    
    private String approveRemark;
    
    private String executeUser;
    
    private Date executeTime;
    
    private String executeResult;
    
    private Integer itemAddCount;
    
    private Integer itemModCount;
    
    private Integer itemDelCount;
    
    private Date createTime;
    
    private Date updateTime;
    
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

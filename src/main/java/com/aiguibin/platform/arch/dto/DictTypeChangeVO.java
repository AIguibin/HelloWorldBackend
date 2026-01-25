package com.aiguibin.platform.arch.dto;

import lombok.Data;
import java.util.Date;

/**
 * 字典类型变更视图对象
 */
@Data
public class DictTypeChangeVO {
    
    private String uuid;
    
    private Long id;
    
    private String changeNo;
    
    private String dctTpId;
    
    private String changeType;
    
    private String oldDctTp;
    
    private String newDctTp;
    
    private String oldDctTpNm;
    
    private String newDctTpNm;
    
    private String applyUserNum;
    
    private String applyUserName;
    
    private Date applyTime;
    
    private String changeReason;
    
    private String changeImpact;
    
    private String approveStatus;
    
    private String executeStatus;
    
    private String approverNum;
    
    private String approverName;
    
    private Date approveTime;
    
    private String approveRemark;
    
    private String executeUserNum;
    
    private String executeUserName;
    
    private Date executeTime;
    
    private String executeResult;
    
    private String flowId;
    
    private String currentNodeId;
    
    private String approvalInstanceId;
    
    private String createdBy;
    
    private Date createdTime;
    
    private String updatedBy;
    
    private Date updatedTime;
    
    private Integer isDeleted;
    
    // 显示用字段
    private String changeTypeName;
    
    private String approveStatusName;
    
    private String executeStatusName;
}
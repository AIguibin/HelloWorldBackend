package com.aiguibin.platform.arch.dto;

import com.aiguibin.platform.arch.entity.DictItemChange;
import com.aiguibin.platform.arch.enums.ApproveStatus;
import com.aiguibin.platform.arch.enums.ExecuteStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 字典变更VO
 */
@Data
public class DictChangeVO {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 变更单号
     */
    private String changeNo;

    /**
     * 关联字典类型ID
     */
    private String dctTpId;

    /**
     * 原始字典类型编码
     */
    private String oldDctTp;

    /**
     * 新字典类型编码
     */
    private String newDctTp;

    /**
     * 原始字典类型名称
     */
    private String oldDctTpNm;

    /**
     * 新字典类型名称
     */
    private String newDctTpNm;

    /**
     * 申请人
     */
    private String applyUser;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 审批状态
     */
    private ApproveStatus approveStatus;

    /**
     * 执行状态
     */
    private ExecuteStatus executeStatus;

    /**
     * 变更原因
     */
    private String changeReason;

    /**
     * 变更影响
     */
    private String changeImpact;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 执行结果
     */
    private String executeResult;

    /**
     * 字典项变更列表
     */
    private List<DictItemChange> itemChanges;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

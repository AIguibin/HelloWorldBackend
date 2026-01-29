package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.Date;

/**
 * 字典项变更明细表实体
 */
@Data
@TableName("biz_ddct_item_change")
public class DictItemChange {
    
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
    
    @TableField("change_id")
    private String changeId;
    
    @TableField("change_no")
    private String changeNo;
    
    @TableField("dict_id")
    private String dictId;
    
    @TableField("change_operation")
    private String changeOperation;
    
    @TableField("old_dct_seq")
    private Integer oldDctSeq;
    
    @TableField("new_dct_seq")
    private Integer newDctSeq;
    
    @TableField("old_dct_grp")
    private String oldDctGrp;
    
    @TableField("new_dct_grp")
    private String newDctGrp;
    
    @TableField("old_dct_key")
    private String oldDctKey;
    
    @TableField("new_dct_key")
    private String newDctKey;
    
    @TableField("old_dct_val_nm")
    private String oldDctValNm;
    
    @TableField("new_dct_val_nm")
    private String newDctValNm;
    
    @TableField("old_dct_tp_nm")
    private String oldDctTpNm;
    
    @TableField("new_dct_tp_nm")
    private String newDctTpNm;
    
    @TableField("old_dct_val")
    private String oldDctVal;
    
    @TableField("new_dct_val")
    private String newDctVal;
    
    @TableField("old_dct_tp")
    private String oldDctTp;
    
    @TableField("new_dct_tp")
    private String newDctTp;
    
    @TableField("old_dct_dsc")
    private String oldDctDsc;
    
    @TableField("new_dct_dsc")
    private String newDctDsc;
    
    @TableField("old_stcd")
    private String oldStcd;
    
    @TableField("new_stcd")
    private String newStcd;
    
    @TableField("execute_status")
    private String executeStatus;
    
    @TableField("execute_result")
    private String executeResult;
    
    @TableField("item_order")
    private Integer itemOrder;
    
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
    private String changeOperationName;
    
    @TableField(exist = false)
    private String executeStatusName;
    
    /**
     * 获取操作类型名称
     */
    public String getChangeOperationName() {
        if (changeOperation != null) {
            switch (changeOperation) {
                case "ADD":
                    return "新增";
                case "MOD":
                    return "修改";
                case "DEL":
                    return "删除";
                default:
                    return changeOperation;
            }
        }
        return changeOperation;
    }
    
    /**
     * 获取执行状态名称
     */
    public String getExecuteStatusName() {
        if (executeStatus != null) {
            switch (executeStatus) {
                case "PENDING":
                    return "待执行";
                case "SUCCESS":
                    return "成功";
                case "FAILED":
                    return "失败";
                default:
                    return executeStatus;
            }
        }
        return executeStatus;
    }
}

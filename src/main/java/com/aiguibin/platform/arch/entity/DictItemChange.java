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
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String changeId;
    
    private String changeOperation;
    
    private Integer oldDctSeq;
    
    private Integer newDctSeq;
    
    private String oldDctGrp;
    
    private String newDctGrp;
    
    private String oldDctKey;
    
    private String newDctKey;
    
    private String oldDctValNm;
    
    private String newDctValNm;
    
    private String oldDctVal;
    
    private String newDctVal;
    
    private String oldDctDsc;
    
    private String newDctDsc;
    
    private String oldStcd;
    
    private String newStcd;
    
    private String executeStatus;
    
    private String executeResult;
    
    private Integer itemOrder;
    
    private Date createTime;
    
    private Date updateTime;
    
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

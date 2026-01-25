package com.aiguibin.platform.arch.util;

import org.springframework.stereotype.Component;
import com.aiguibin.platform.arch.dto.DictChangeApplyDTO;
import com.aiguibin.platform.arch.dto.DictItemChangeDTO;
import com.aiguibin.platform.arch.dto.DictItemDataDTO;
import com.aiguibin.platform.arch.dto.DictTypeChangeDTO;
import com.aiguibin.platform.arch.enums.ChangeType;

/**
 * 字典变更验证器
 */
@Component
public class DictChangeValidator {
    
    /**
     * 验证变更申请
     */
    public boolean validate(DictChangeApplyDTO dto) {
        // 1. 验证基本信息
        if (dto == null) {
            return false;
        }
        
        // 2. 验证变更类型
        if (dto.getChangeType() == null) {
            return false;
        }
        
        // 3. 验证变更原因
        if (dto.getChangeReason() == null || dto.getChangeReason().isEmpty()) {
            return false;
        }
        
        // 4. 验证字典类型信息
        if (ChangeType.MOD.equals(dto.getChangeType()) || ChangeType.DEL.equals(dto.getChangeType())) {
            if (dto.getDictTypeId() == null || dto.getDictTypeId().isEmpty()) {
                return false;
            }
        }
        
        if (ChangeType.ADD.equals(dto.getChangeType()) || ChangeType.MOD.equals(dto.getChangeType())) {
            DictTypeChangeDTO typeChange = dto.getTypeChange();
            if (typeChange == null) {
                return false;
            }
            if (typeChange.getNewDctTp() == null || typeChange.getNewDctTp().isEmpty()) {
                return false;
            }
            if (typeChange.getNewDctTpNm() == null || typeChange.getNewDctTpNm().isEmpty()) {
                return false;
            }
        }
        
        // 5. 验证字典项变更
        if (dto.getItemChanges() != null && !dto.getItemChanges().isEmpty()) {
            for (DictItemChangeDTO itemChange : dto.getItemChanges()) {
                if (!validateItemChange(itemChange)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 验证字典项变更
     */
    private boolean validateItemChange(DictItemChangeDTO itemChange) {
        if (itemChange == null) {
            return false;
        }
        
        // 验证操作类型
        if (itemChange.getChangeOperation() == null || itemChange.getChangeOperation().isEmpty()) {
            return false;
        }
        
        // 验证字典项信息
        if ("ADD".equals(itemChange.getChangeOperation()) || "MOD".equals(itemChange.getChangeOperation())) {
            DictItemDataDTO newData = itemChange.getNewData();
            if (newData == null) {
                return false;
            }
            if (newData.getDctKey() == null || newData.getDctKey().isEmpty()) {
                return false;
            }
            if (newData.getDctValNm() == null || newData.getDctValNm().isEmpty()) {
                return false;
            }
            if (newData.getDctVal() == null || newData.getDctVal().isEmpty()) {
                return false;
            }
        }
        
        if ("MOD".equals(itemChange.getChangeOperation()) || "DEL".equals(itemChange.getChangeOperation())) {
            if (itemChange.getDictId() == null || itemChange.getDictId().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
}
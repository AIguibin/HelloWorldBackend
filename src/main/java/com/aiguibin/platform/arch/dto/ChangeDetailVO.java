package com.aiguibin.platform.arch.dto;

import com.aiguibin.platform.arch.entity.DictItemChange;
import lombok.Data;
import java.util.List;

/**
 * 变更详情视图对象
 */
@Data
public class ChangeDetailVO {
    
    /**
     * 字典类型变更信息
     */
    private DictTypeChangeVO dictTypeChange;
    
    /**
     * 字典项变更列表
     */
    private List<DictItemChange> dictItemChanges;
}
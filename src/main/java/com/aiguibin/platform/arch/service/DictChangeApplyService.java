package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.dto.DictChangeApplyDTO;
import com.aiguibin.platform.arch.dto.DictTypeChangeVO;
import com.aiguibin.platform.arch.dto.DictItemImportVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 变更申请服务
 */
public interface DictChangeApplyService {

    /**
     * 提交变更申请
     */
    DictTypeChangeVO applyChange(DictChangeApplyDTO dto);

    /**
     * 保存草稿
     */
    DictTypeChangeVO saveDraft(DictChangeApplyDTO dto);

    /**
     * 导入字典项
     */
    DictItemImportVO importDictItems(MultipartFile file, String dictType);
}

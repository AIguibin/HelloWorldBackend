package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.dto.ChangeQueryDTO;
import com.aiguibin.platform.arch.dto.ChangeDetailVO;
import com.aiguibin.platform.arch.dto.DictChangeVO;
import com.aiguibin.platform.arch.dto.DictTypeDetailVO;
import com.aiguibin.platform.arch.dto.DictTypeVO;
import com.aiguibin.platform.arch.dto.PageResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 变更查询服务
 */
public interface DictChangeQueryService {

    /**
     * 查询变更记录
     */
    PageResult<DictChangeVO> queryChanges(ChangeQueryDTO queryDTO);

    /**
     * 获取变更详情
     */
    ChangeDetailVO getChangeDetail(String changeId);

    /**
     * 获取待审批列表
     */
    List<DictChangeVO> getPendingApprove();

    /**
     * 获取待执行列表
     */
    List<DictChangeVO> getPendingExecute();

    /**
     * 导出变更记录
     */
    void exportChanges(ChangeQueryDTO queryDTO, HttpServletResponse response);

    /**
     * 获取字典类型列表
     */
    List<DictTypeVO> getDictTypes();

    /**
     * 加载字典类型详情
     */
    DictTypeDetailVO loadDictType(String dictTypeId);
}

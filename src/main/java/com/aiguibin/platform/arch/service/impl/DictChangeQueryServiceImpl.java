package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.dto.ChangeQueryDTO;
import com.aiguibin.platform.arch.entity.DictItemChange;
import com.aiguibin.platform.arch.entity.DictTypeChange;
import com.aiguibin.platform.arch.mapper.DictItemChangeMapper;
import com.aiguibin.platform.arch.mapper.DictTypeChangeMapper;
import com.aiguibin.platform.arch.service.DictChangeQueryService;
import com.aiguibin.platform.arch.dto.ChangeDetailVO;
import com.aiguibin.platform.arch.dto.DictChangeVO;
import com.aiguibin.platform.arch.dto.DictTypeChangeVO;
import com.aiguibin.platform.arch.dto.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 变更查询服务实现
 */
@Service
@Slf4j
public class DictChangeQueryServiceImpl implements DictChangeQueryService {

    @Autowired
    private DictTypeChangeMapper changeMapper;

    @Autowired
    private DictItemChangeMapper itemChangeMapper;

    @Override
    public PageResult<DictChangeVO> queryChanges(ChangeQueryDTO queryDTO) {
        // 1. 计算分页参数
        int offset = (queryDTO.getPage() - 1) * queryDTO.getSize();

        // 2. 查询变更记录
        List<DictTypeChange> changes = changeMapper.selectByPage(offset, queryDTO.getSize());
        Long total = changeMapper.selectCount();

        // 3. 转换为VO
        List<DictChangeVO> vos = new ArrayList<>();
        for (DictTypeChange change : changes) {
            DictChangeVO vo = convertToVO(change);
            vos.add(vo);
        }

        // 4. 构建分页结果
        PageResult<DictChangeVO> result = new PageResult<>();
        result.setRecords(vos);
        result.setTotal(total);

        return result;
    }

    @Override
    public ChangeDetailVO getChangeDetail(String changeId) {
        // 1. 获取变更记录
        DictTypeChange change = changeMapper.selectById(changeId);
        if (change == null) {
            throw new RuntimeException("变更记录不存在");
        }

        // 2. 获取字典项变更
        List<DictItemChange> itemChanges = itemChangeMapper.selectByChangeId(changeId);

        // 3. 构建详情VO
        ChangeDetailVO detail = new ChangeDetailVO();
        
        // 转换DictTypeChange为DictTypeChangeVO
        DictTypeChangeVO dictTypeChangeVO = new DictTypeChangeVO();
        BeanUtils.copyProperties(change, dictTypeChangeVO);
        detail.setDictTypeChange(dictTypeChangeVO);
        
        // 设置字典项变更
        detail.setDictItemChanges(itemChanges);

        return detail;
    }

    @Override
    public List<DictChangeVO> getPendingApprove() {
        // 1. 查询待审批记录
        List<DictTypeChange> changes = changeMapper.selectPendingApproval();

        // 2. 转换为VO
        List<DictChangeVO> vos = new ArrayList<>();
        for (DictTypeChange change : changes) {
            DictChangeVO vo = convertToVO(change);
            vos.add(vo);
        }

        return vos;
    }

    @Override
    public List<DictChangeVO> getPendingExecute() {
        // 1. 查询待执行记录
        List<DictTypeChange> changes = changeMapper.selectPendingExecute();

        // 2. 转换为VO
        List<DictChangeVO> vos = new ArrayList<>();
        for (DictTypeChange change : changes) {
            DictChangeVO vo = convertToVO(change);
            vos.add(vo);
        }

        return vos;
    }

    @Override
    public void exportChanges(ChangeQueryDTO queryDTO, HttpServletResponse response) {
        // TODO: 实现导出逻辑
        log.info("导出变更记录");
    }

    private DictChangeVO convertToVO(DictTypeChange change) {
        DictChangeVO vo = new DictChangeVO();
        BeanUtils.copyProperties(change, vo);
        return vo;
    }
}

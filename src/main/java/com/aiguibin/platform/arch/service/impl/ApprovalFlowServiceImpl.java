package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.ApprovalFlow;
import com.aiguibin.platform.arch.mapper.ApprovalFlowMapper;
import com.aiguibin.platform.arch.service.ApprovalFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 审批流程Service实现类
 * 实现审批流程的查询、新增、修改、删除等功能
 */
@Service
public class ApprovalFlowServiceImpl implements ApprovalFlowService {

    @Resource
    private ApprovalFlowMapper approvalFlowMapper;

    @Override
    public List<ApprovalFlow> getAllApprovalFlows() {
        LambdaQueryWrapper<ApprovalFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalFlow::getIsDeleted, 0)
                .orderByAsc(ApprovalFlow::getFlowName);
        return approvalFlowMapper.selectList(queryWrapper);
    }

    @Override
    public ApprovalFlow getApprovalFlowById(String flowId) {
        LambdaQueryWrapper<ApprovalFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalFlow::getFlowId, flowId)
                .eq(ApprovalFlow::getIsDeleted, 0);
        return approvalFlowMapper.selectOne(queryWrapper);
    }

    @Override
    public ApprovalFlow getDefaultFlowByBusinessType(String businessType) {
        LambdaQueryWrapper<ApprovalFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalFlow::getBusinessType, businessType)
                .eq(ApprovalFlow::getIsDefault, 1)
                .eq(ApprovalFlow::getIsActive, 1)
                .eq(ApprovalFlow::getIsDeleted, 0);
        return approvalFlowMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<ApprovalFlow> getApprovalFlowsPage(int page, int size) {
        LambdaQueryWrapper<ApprovalFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalFlow::getIsDeleted, 0)
                .orderByAsc(ApprovalFlow::getFlowName);
        return approvalFlowMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public boolean addApprovalFlow(ApprovalFlow approvalFlow) {
        // 验证必填字段
        if (approvalFlow.getFlowId() == null || approvalFlow.getFlowId().isEmpty() ||
                approvalFlow.getFlowName() == null || approvalFlow.getFlowName().isEmpty() ||
                approvalFlow.getBusinessType() == null || approvalFlow.getBusinessType().isEmpty()) {
            return false;
        }

        // 设置默认值
        if (approvalFlow.getIsActive() == null) {
            approvalFlow.setIsActive(1);
        }
        if (approvalFlow.getIsDefault() == null) {
            approvalFlow.setIsDefault(0);
        }
        if (approvalFlow.getIsDeleted() == null) {
            approvalFlow.setIsDeleted(0);
        }

        // 如果是默认流程，将其他同类型流程设为非默认
        if (approvalFlow.getIsDefault() == 1) {
            LambdaQueryWrapper<ApprovalFlow> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(ApprovalFlow::getBusinessType, approvalFlow.getBusinessType())
                    .eq(ApprovalFlow::getIsDefault, 1)
                    .eq(ApprovalFlow::getIsDeleted, 0);
            ApprovalFlow updateFlow = new ApprovalFlow();
            updateFlow.setIsDefault(0);
            approvalFlowMapper.update(updateFlow, updateWrapper);
        }

        return approvalFlowMapper.insert(approvalFlow) > 0;
    }

    @Override
    public boolean updateApprovalFlow(ApprovalFlow approvalFlow) {
        // 验证必填字段
        if (approvalFlow.getId() == null || approvalFlow.getId() <= 0) {
            return false;
        }

        // 如果是默认流程，将其他同类型流程设为非默认
        if (approvalFlow.getIsDefault() == 1) {
            LambdaQueryWrapper<ApprovalFlow> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(ApprovalFlow::getBusinessType, approvalFlow.getBusinessType())
                    .eq(ApprovalFlow::getIsDefault, 1)
                    .ne(ApprovalFlow::getId, approvalFlow.getId())
                    .eq(ApprovalFlow::getIsDeleted, 0);
            ApprovalFlow updateFlow = new ApprovalFlow();
            updateFlow.setIsDefault(0);
            approvalFlowMapper.update(updateFlow, updateWrapper);
        }

        return approvalFlowMapper.updateById(approvalFlow) > 0;
    }

    @Override
    public boolean deleteApprovalFlow(String flowId) {
        LambdaQueryWrapper<ApprovalFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalFlow::getFlowId, flowId)
                .eq(ApprovalFlow::getIsDeleted, 0);
        ApprovalFlow approvalFlow = approvalFlowMapper.selectOne(queryWrapper);
        if (approvalFlow == null) {
            return false;
        }

        // 逻辑删除
        approvalFlow.setIsDeleted(1);
        return approvalFlowMapper.updateById(approvalFlow) > 0;
    }
}

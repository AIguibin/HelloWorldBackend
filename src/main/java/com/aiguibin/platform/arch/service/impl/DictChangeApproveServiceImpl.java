package com.aiguibin.platform.arch.service.impl;

import cn.hutool.core.date.DateUtil;
import com.aiguibin.platform.arch.dto.ApproveDTO;
import com.aiguibin.platform.arch.entity.DictTypeChange;
import com.aiguibin.platform.arch.enums.ApproveStatus;
import com.aiguibin.platform.arch.mapper.DictTypeChangeMapper;
import com.aiguibin.platform.arch.service.DictChangeApproveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 变更审批服务实现
 */
@Service
@Slf4j
public class DictChangeApproveServiceImpl implements DictChangeApproveService {

    @Autowired
    private DictTypeChangeMapper changeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveChange(String changeId, ApproveDTO dto) {
        // 1. 获取变更记录
        DictTypeChange change = changeMapper.selectById(changeId);
        if (change == null) {
            throw new RuntimeException("变更记录不存在");
        }

        // 2. 验证审批状态
        if (!ApproveStatus.PENDING.name().equals(change.getApproveStatus())) {
            throw new RuntimeException("当前状态不可审批");
        }

        // 3. 更新审批状态
        String approveStatus = dto.getApproveResult() ? ApproveStatus.APPROVED.name() : ApproveStatus.REJECTED.name();
        changeMapper.updateApproveStatus(
                changeId,
                approveStatus,
                getCurrentUser(),
                DateUtil.now(),
                dto.getApproveRemark()
        );

        // 4. 记录操作日志
        logOperation(change, "审批" + (dto.getApproveResult() ? "通过" : "拒绝"));

        // 5. 发送通知
        sendApproveNotification(change, dto.getApproveResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeApprove(String changeId) {
        // 1. 获取变更记录
        DictTypeChange change = changeMapper.selectById(changeId);
        if (change == null) {
            throw new RuntimeException("变更记录不存在");
        }

        // 2. 验证撤回条件
        if (!ApproveStatus.APPROVED.name().equals(change.getApproveStatus())) {
            throw new RuntimeException("只有已通过的审批可以撤回");
        }

        // 3. 更新审批状态为待审批
        changeMapper.updateApproveStatus(
                changeId,
                ApproveStatus.PENDING.name(),
                getCurrentUser(),
                DateUtil.now(),
                "撤回审批"
        );

        // 4. 记录操作日志
        logOperation(change, "撤回审批");

        // 5. 发送通知
        sendRevokeNotification(change);
    }

    private void logOperation(DictTypeChange change, String operation) {
        // TODO: 实现操作日志记录
        log.info("{}: changeNo={}, approver={}", operation, change.getChangeNo(), getCurrentUser());
    }

    private void sendApproveNotification(DictTypeChange change, boolean approveResult) {
        // TODO: 实现通知发送
        log.info("发送审批通知: changeNo={}, result={}", change.getChangeNo(), approveResult ? "通过" : "拒绝");
    }

    private void sendRevokeNotification(DictTypeChange change) {
        // TODO: 实现通知发送
        log.info("发送撤回通知: changeNo={}", change.getChangeNo());
    }

    private String getCurrentUser() {
        // TODO: 从上下文获取当前用户
        return "admin";
    }
}

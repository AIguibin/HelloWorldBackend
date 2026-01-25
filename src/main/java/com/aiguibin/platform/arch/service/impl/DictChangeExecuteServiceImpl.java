package com.aiguibin.platform.arch.service.impl;

import cn.hutool.core.date.DateUtil;
import com.aiguibin.platform.arch.entity.DictItemChange;
import com.aiguibin.platform.arch.entity.DictTypeChange;
import com.aiguibin.platform.arch.enums.ApproveStatus;
import com.aiguibin.platform.arch.enums.ExecuteStatus;
import com.aiguibin.platform.arch.mapper.DictItemChangeMapper;
import com.aiguibin.platform.arch.mapper.DictTypeChangeMapper;
import com.aiguibin.platform.arch.service.DictChangeExecuteService;
import com.aiguibin.platform.arch.dto.ExecuteResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 变更执行服务实现
 */
@Service
@Slf4j
public class DictChangeExecuteServiceImpl implements DictChangeExecuteService {

    @Autowired
    private DictTypeChangeMapper changeMapper;

    @Autowired
    private DictItemChangeMapper itemChangeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecuteResultVO executeChange(String changeId) {
        // 1. 获取变更记录
        DictTypeChange change = changeMapper.selectById(changeId);
        if (change == null) {
            throw new RuntimeException("变更记录不存在");
        }

        // 2. 验证执行条件
        if (!ApproveStatus.APPROVED.name().equals(change.getApproveStatus())) {
            throw new RuntimeException("未审批通过，不可执行");
        }
        if (!ExecuteStatus.PENDING.name().equals(change.getExecuteStatus())) {
            throw new RuntimeException("当前状态不可执行");
        }

        // 3. 更新执行状态为执行中
        changeMapper.updateExecuteStatus(
                changeId,
                ExecuteStatus.EXECUTING.name(),
                getCurrentUser(),
                DateUtil.now(),
                "开始执行"
        );

        ExecuteResultVO result = new ExecuteResultVO();
        try {
            // 4. 执行字典类型变更
            executeDictTypeChange(change);

            // 5. 执行字典项变更
            executeDictItemChanges(changeId);

            // 6. 更新执行状态为成功
            changeMapper.updateExecuteStatus(
                    changeId,
                    ExecuteStatus.SUCCESS.name(),
                    getCurrentUser(),
                    DateUtil.now(),
                    "执行成功"
            );

            result.setSuccess(true);
            result.setMessage("执行成功");

        } catch (Exception e) {
            // 7. 更新执行状态为失败
            changeMapper.updateExecuteStatus(
                    changeId,
                    ExecuteStatus.FAILED.name(),
                    getCurrentUser(),
                    DateUtil.now(),
                    "执行失败: " + e.getMessage()
            );

            result.setSuccess(false);
            result.setMessage("执行失败: " + e.getMessage());
            throw e;
        } finally {
            // 8. 记录操作日志
            logOperation(change, "执行" + (result.isSuccess() ? "成功" : "失败"));

            // 9. 发送通知
            sendExecuteNotification(change, result.isSuccess());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelExecute(String changeId) {
        // 1. 获取变更记录
        DictTypeChange change = changeMapper.selectById(changeId);
        if (change == null) {
            throw new RuntimeException("变更记录不存在");
        }

        // 2. 验证取消条件
        if (!ExecuteStatus.PENDING.name().equals(change.getExecuteStatus())) {
            throw new RuntimeException("只有待执行的变更可以取消");
        }

        // 3. 更新执行状态为取消
        changeMapper.updateExecuteStatus(
                changeId,
                "CANCELED",
                getCurrentUser(),
                DateUtil.now(),
                "取消执行"
        );

        // 4. 记录操作日志
        logOperation(change, "取消执行");

        // 5. 发送通知
        sendCancelNotification(change);
    }

    private void executeDictTypeChange(DictTypeChange change) {
        // TODO: 实现字典类型变更逻辑
        log.info("执行字典类型变更: changeNo={}, changeType={}", change.getChangeNo(), change.getChangeType());
    }

    private void executeDictItemChanges(String changeId) {
        // 1. 获取字典项变更列表
        List<DictItemChange> itemChanges = itemChangeMapper.selectByChangeId(changeId);
        if (itemChanges.isEmpty()) {
            return;
        }

        // 2. 批量执行变更
        for (DictItemChange itemChange : itemChanges) {
            try {
                executeDictItemChange(itemChange);
                itemChange.setExecuteStatus(ExecuteStatus.SUCCESS.name());
                itemChange.setExecuteResult("执行成功");
            } catch (Exception e) {
                itemChange.setExecuteStatus(ExecuteStatus.FAILED.name());
                itemChange.setExecuteResult("执行失败: " + e.getMessage());
                throw e;
            }
        }

        // 3. 批量更新执行状态
        itemChangeMapper.batchUpdateExecuteStatus(itemChanges);
    }

    private void executeDictItemChange(DictItemChange itemChange) {
        // TODO: 实现字典项变更逻辑
        log.info("执行字典项变更: operation={}, key={}", itemChange.getChangeOperation(), itemChange.getNewDctKey());
    }

    private void logOperation(DictTypeChange change, String operation) {
        // TODO: 实现操作日志记录
        log.info("{}: changeNo={}, executor={}", operation, change.getChangeNo(), getCurrentUser());
    }

    private void sendExecuteNotification(DictTypeChange change, boolean success) {
        // TODO: 实现通知发送
        log.info("发送执行通知: changeNo={}, result={}", change.getChangeNo(), success ? "成功" : "失败");
    }

    private void sendCancelNotification(DictTypeChange change) {
        // TODO: 实现通知发送
        log.info("发送取消执行通知: changeNo={}", change.getChangeNo());
    }

    private String getCurrentUser() {
        // TODO: 从上下文获取当前用户
        return "admin";
    }
}

package com.aiguibin.online.table.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.online.table.entity.CodeScriptChangeHistory;
import com.aiguibin.online.table.entity.CodeScriptChangeRecord;
import com.aiguibin.online.table.mapper.CodeScriptChangeHistoryMapper;
import com.aiguibin.online.table.mapper.CodeScriptChangeRecordMapper;
import com.aiguibin.online.table.mapper.OperationLogMapper;
import com.aiguibin.online.table.entity.OperationLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CodeScriptChangeRecordService {
    @Resource
    private CodeScriptChangeRecordMapper recordMapper;
    @Resource
    private CodeScriptChangeHistoryMapper historyMapper;
    @Resource
    private OperationLogMapper operationLogMapper;

    public Page<CodeScriptChangeRecord> page(int page, int size,
                                             String groupName,
                                             String developer,
                                             String developType,
                                             String currentStatus,
                                             String serviceName,
                                             String defectNumber,
                                             LocalDateTime startTime,
                                             LocalDateTime endTime) {
        LambdaQueryWrapper<CodeScriptChangeRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(CodeScriptChangeRecord::getIsDeleted, 0);
        if (groupName != null && !groupName.isEmpty()) qw.like(CodeScriptChangeRecord::getGroupName, groupName);
        if (developer != null && !developer.isEmpty()) qw.like(CodeScriptChangeRecord::getDeveloper, developer);
        if (developType != null && !developType.isEmpty()) qw.like(CodeScriptChangeRecord::getDevelopType, developType);
        if (currentStatus != null && !currentStatus.isEmpty()) qw.like(CodeScriptChangeRecord::getCurrentStatus, currentStatus);
        if (serviceName != null && !serviceName.isEmpty()) qw.like(CodeScriptChangeRecord::getServiceName, serviceName);
        if (defectNumber != null && !defectNumber.isEmpty()) qw.like(CodeScriptChangeRecord::getDefectNumber, defectNumber);
        // 移除 isReleased 过滤，统一使用 currentStatus（若后续需要可扩展）
        // 按发版日期范围筛选（若提供）
        if (startTime != null) qw.ge(CodeScriptChangeRecord::getReleaseDate, startTime.toLocalDate());
        if (endTime != null) qw.le(CodeScriptChangeRecord::getReleaseDate, endTime.toLocalDate());
        qw.orderByDesc(CodeScriptChangeRecord::getCreateTime);
        return recordMapper.selectPage(new Page<>(page, size), qw);
    }

    public java.util.List<CodeScriptChangeRecord> listForExport(String status,
                                                                LocalDateTime strTime,
                                                                LocalDateTime endTime,
                                                                int limit) {
        LambdaQueryWrapper<CodeScriptChangeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CodeScriptChangeRecord::getIsDeleted, 0);
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(CodeScriptChangeRecord::getCurrentStatus, status.trim());
        }
        if (strTime != null) {
            wrapper.ge(CodeScriptChangeRecord::getReleaseDate, strTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(CodeScriptChangeRecord::getReleaseDate, endTime.toLocalDate());
        }
        wrapper.orderByDesc(CodeScriptChangeRecord::getCreateTime);
        Page<CodeScriptChangeRecord> page = recordMapper.selectPage(new Page<>(1, Math.max(1, limit)), wrapper);
        return page.getRecords();
    }

    public CodeScriptChangeRecord getById(Long id) {
        return recordMapper.selectById(id);
    }

    @Transactional
    public Long create(CodeScriptChangeRecord r, String operator, String pagePath, String buttonName, String ipAddress) {
        // 版本号：yyyyMMdd-<epochMillis>
        r.setVersion(generateVersion());
        if (r.getCurrentStatus() == null || r.getCurrentStatus().trim().isEmpty()) {
            r.setCurrentStatus("待审批");
        }
        r.setCreateUser(operator);
        r.setUpdateUser(operator);
        recordMapper.insert(r);
        saveHistory(r, "CREATE", operator, "创建记录");
        // 操作日志
        saveOpLog(operator, "CREATE", "CodeScriptChangeRecord", r.getId(), "OK", "创建成功", pagePath, buttonName, ipAddress);
        return r.getId();
    }

    @Transactional
    public boolean update(Long id, CodeScriptChangeRecord newData, String operator, String pagePath, String buttonName, String ipAddress) {
        CodeScriptChangeRecord before = recordMapper.selectById(id);
        if (before == null) {
            saveOpLog(operator, "UPDATE", "CodeScriptChangeRecord", id, "FAIL", "记录不存在", pagePath, buttonName, ipAddress);
            return false;
        }
        // 写入更新前快照
        saveHistory(before, "UPDATE", operator, "更新记录(前快照)");
        newData.setId(id);
        newData.setUpdateUser(operator);
        // 每次更新都重置版本号
        newData.setVersion(generateVersion());
        int rows = recordMapper.updateById(newData);
        saveOpLog(operator, "UPDATE", "CodeScriptChangeRecord", id, rows > 0 ? "OK" : "FAIL", rows > 0 ? "更新成功" : "更新失败", pagePath, buttonName, ipAddress);
        return rows > 0;
    }

    @Transactional
    public boolean logicalDelete(Long id, String operator, String pagePath, String buttonName, String ipAddress) {
        CodeScriptChangeRecord before = recordMapper.selectById(id);
        if (before == null) {
            saveOpLog(operator, "DELETE", "CodeScriptChangeRecord", id, "FAIL", "记录不存在", pagePath, buttonName, ipAddress);
            return false;
        }
        // 写入删除前快照
        saveHistory(before, "DELETE", operator, "逻辑删除(前快照)");
        LambdaUpdateWrapper<CodeScriptChangeRecord> uw = new LambdaUpdateWrapper<>();
        uw.eq(CodeScriptChangeRecord::getId, id)
          .set(CodeScriptChangeRecord::getIsDeleted, 1)
          .set(CodeScriptChangeRecord::getUpdateUser, operator);
        int rows = recordMapper.update(null, uw);
        saveOpLog(operator, "DELETE", "CodeScriptChangeRecord", id, rows > 0 ? "OK" : "FAIL", rows > 0 ? "删除成功" : "删除失败", pagePath, buttonName, ipAddress);
        return rows > 0;
    }

    public Page<CodeScriptChangeHistory> history(Long recordId, int page, int size) {
        LambdaQueryWrapper<CodeScriptChangeHistory> qw = new LambdaQueryWrapper<>();
        qw.eq(CodeScriptChangeHistory::getRecordId, recordId)
          .orderByDesc(CodeScriptChangeHistory::getOperationTime);
        return historyMapper.selectPage(new Page<>(page, size), qw);
    }

    private void saveHistory(CodeScriptChangeRecord src, String opType, String operator, String desc) {
        CodeScriptChangeHistory h = new CodeScriptChangeHistory();
        h.setRecordId(src.getId());
        h.setOperationType(opType);
        h.setOperationUser(operator);
        h.setOperationTime(LocalDateTime.now());
        h.setOperationDescription(desc);
        // 移除 isReleased
        h.setReleaseDate(src.getReleaseDate());
        h.setDefectNumber(src.getDefectNumber());
        h.setGroupName(src.getGroupName());
        h.setDeveloper(src.getDeveloper());
        h.setDevelopType(src.getDevelopType());
        h.setBranchName(src.getBranchName());
        h.setServiceName(src.getServiceName());
        h.setProblemDescription(src.getProblemDescription());
        h.setImpactAnalysis(src.getImpactAnalysis());
        h.setSolution(src.getSolution());
        h.setInvolveExternalSystem(src.getInvolveExternalSystem());
        h.setCrossService(src.getCrossService());
        h.setCodeList(src.getCodeList());
        h.setRemark(src.getRemark());
        h.setVersion(src.getVersion());
        h.setChangeDesc(src.getChangeDesc());
        h.setCurrentStatus(src.getCurrentStatus());
        h.setCreateTime(src.getCreateTime());
        h.setUpdateTime(src.getUpdateTime());
        h.setCreateUser(src.getCreateUser());
        h.setUpdateUser(src.getUpdateUser());
        h.setIsDeleted(src.getIsDeleted());
        historyMapper.insert(h);
    }

    private String generateVersion() {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + String.valueOf(System.currentTimeMillis());
    }

    private void saveOpLog(String operator, String type, String objType, Long objId, String result, String msg, String pagePath, String buttonName, String ip) {
        OperationLog log = new OperationLog();
        log.setOperator(operator);
        log.setOperationType(type);
        log.setObjectType(objType);
        log.setObjectId(objId);
        log.setResult(result);
        log.setMessage(msg);
        log.setOperationTime(LocalDateTime.now());
        log.setPagePath(pagePath);
        log.setButtonName(buttonName);
        log.setIpAddress(ip);
        operationLogMapper.insert(log);
    }
}
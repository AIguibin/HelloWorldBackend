package com.aiguibin.platform.arch.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.ChangeHistory;
import com.aiguibin.platform.arch.entity.ChangeRecord;
import com.aiguibin.platform.arch.mapper.ChangeHistoryMapper;
import com.aiguibin.platform.arch.mapper.ChangeRecordMapper;
import com.aiguibin.platform.arch.mapper.OperationLogMapper;
import com.aiguibin.platform.arch.entity.OperationLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ChangeRecordService {
    @Resource
    private ChangeRecordMapper recordMapper;
    @Resource
    private ChangeHistoryMapper historyMapper;
    @Resource
    private OperationLogMapper operationLogMapper;

    public Page<ChangeRecord> page(int page, int size,
                                             String groupName,
                                             String developerNum,
                                             String developType,
                                             String currentStatus,
                                             String serviceName,
                                             String defectNumber,
                                             LocalDateTime startTime,
                                             LocalDateTime endTime) {
        LambdaQueryWrapper<ChangeRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(ChangeRecord::getIsDeleted, 0);
        if (groupName != null && !groupName.isEmpty()) qw.like(ChangeRecord::getGroupName, groupName);
        if (developerNum != null && !developerNum.isEmpty()) qw.like(ChangeRecord::getDeveloperNum, developerNum);
        if (developType != null && !developType.isEmpty()) qw.like(ChangeRecord::getDevelopType, developType);
        if (currentStatus != null && !currentStatus.isEmpty()) qw.like(ChangeRecord::getCurrentStatus, currentStatus);
        if (serviceName != null && !serviceName.isEmpty()) qw.like(ChangeRecord::getServiceName, serviceName);
        if (defectNumber != null && !defectNumber.isEmpty()) qw.like(ChangeRecord::getDefectNumber, defectNumber);
        // 按发版日期范围筛选（若提供）
        if (startTime != null) qw.ge(ChangeRecord::getReleaseDate, startTime.toLocalDate());
        if (endTime != null) qw.le(ChangeRecord::getReleaseDate, endTime.toLocalDate());
        qw.orderByDesc(ChangeRecord::getCreatedTime);
        return recordMapper.selectPage(new Page<>(page, size), qw);
    }

    public java.util.List<ChangeRecord> listForExport(String status,
                                                                LocalDateTime strTime,
                                                                LocalDateTime endTime,
                                                                int limit) {
        LambdaQueryWrapper<ChangeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChangeRecord::getIsDeleted, 0);
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(ChangeRecord::getCurrentStatus, status.trim());
        }
        if (strTime != null) {
            wrapper.ge(ChangeRecord::getReleaseDate, strTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(ChangeRecord::getReleaseDate, endTime.toLocalDate());
        }
        wrapper.orderByDesc(ChangeRecord::getCreatedTime);
        Page<ChangeRecord> page = recordMapper.selectPage(new Page<>(1, Math.max(1, limit)), wrapper);
        return page.getRecords();
    }

    public ChangeRecord getById(Long id) {
        return recordMapper.selectById(id);
    }

    @Transactional
    public Long create(ChangeRecord r, String operator, String pagePath, String buttonName, String ipAddress) {
        // 版本号：yyyyMMdd-<epochMillis>
        r.setVersion(generateVersion());
        if (r.getCurrentStatus() == null || r.getCurrentStatus().trim().isEmpty()) {
            r.setCurrentStatus("待审批");
        }
        r.setCreatedBy(operator);
        r.setUpdatedBy(operator);
        recordMapper.insert(r);
        saveHistory(r, "CREATE", operator, "创建记录");
        // 操作日志
        saveOpLog(operator, "CREATE", "ChangeRecord", r.getId(), "OK", "创建成功", pagePath, buttonName, ipAddress);
        return r.getId();
    }

    @Transactional
    public boolean update(Long id, ChangeRecord newData, String operator, String pagePath, String buttonName, String ipAddress) {
        ChangeRecord before = recordMapper.selectById(id);
        if (before == null) {
            saveOpLog(operator, "UPDATE", "ChangeRecord", id, "FAIL", "记录不存在", pagePath, buttonName, ipAddress);
            return false;
        }
        // 写入更新前快照
        saveHistory(before, "UPDATE", operator, "更新记录(前快照)");
        newData.setId(id);
        newData.setUpdatedBy(operator);
        // 每次更新都重置版本号
        newData.setVersion(generateVersion());
        int rows = recordMapper.updateById(newData);
        saveOpLog(operator, "UPDATE", "ChangeRecord", id, rows > 0 ? "OK" : "FAIL", rows > 0 ? "更新成功" : "更新失败", pagePath, buttonName, ipAddress);
        return rows > 0;
    }

    @Transactional
    public boolean logicalDelete(Long id, String operator, String pagePath, String buttonName, String ipAddress) {
        ChangeRecord before = recordMapper.selectById(id);
        if (before == null) {
            saveOpLog(operator, "DELETE", "ChangeRecord", id, "FAIL", "记录不存在", pagePath, buttonName, ipAddress);
            return false;
        }
        // 写入删除前快照
        saveHistory(before, "DELETE", operator, "逻辑删除(前快照)");
        LambdaUpdateWrapper<ChangeRecord> uw = new LambdaUpdateWrapper<>();
        uw.eq(ChangeRecord::getId, id)
          .set(ChangeRecord::getIsDeleted, 1)
          .set(ChangeRecord::getUpdatedBy, operator);
        int rows = recordMapper.update(null, uw);
        saveOpLog(operator, "DELETE", "ChangeRecord", id, rows > 0 ? "OK" : "FAIL", rows > 0 ? "删除成功" : "删除失败", pagePath, buttonName, ipAddress);
        return rows > 0;
    }

    public Page<ChangeHistory> history(Long recordId, int page, int size) {
        LambdaQueryWrapper<ChangeHistory> qw = new LambdaQueryWrapper<>();
        qw.eq(ChangeHistory::getRecordId, recordId)
          .orderByDesc(ChangeHistory::getOperationTime);
        return historyMapper.selectPage(new Page<>(page, size), qw);
    }

    private void saveHistory(ChangeRecord src, String opType, String operator, String desc) {
        ChangeHistory h = new ChangeHistory();
        h.setRecordId(src.getId());
        h.setRecordCode(src.getRecordCode());
        h.setOperationType(opType);
        // 从operator中解析出用户编号和用户名，格式为 "userNum|userName"
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length == 2) {
            h.setOperationUserNum(operatorParts[0]);
            h.setOperationUserName(operatorParts[1]);
        }
        h.setOperationTime(LocalDateTime.now());
        h.setOperationDescription(desc);
        h.setCurrentStatus(src.getCurrentStatus());
        h.setReleaseDate(src.getReleaseDate());
        h.setDefectNumber(src.getDefectNumber());
        h.setGroupName(src.getGroupName());
        h.setDeveloperNum(src.getDeveloperNum());
        h.setDeveloperName(src.getDeveloperName());
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
        h.setDevelopType(src.getDevelopType());
        h.setOrgCode(src.getOrgCode());
        h.setDeptCode(src.getDeptCode());
        h.setApproverNum(src.getApproverNum());
        h.setApproverName(src.getApproverName());
        h.setApprovalTime(src.getApprovalTime());
        h.setApprovalRemark(src.getApprovalRemark());
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
package com.aiguibin.platform.arch.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;

import com.aiguibin.platform.arch.dto.DictChangeApplyDTO;
import com.aiguibin.platform.arch.dto.DictItemChangeDTO;
import com.aiguibin.platform.arch.entity.DictItemChange;
import com.aiguibin.platform.arch.entity.DictTypeChange;
import com.aiguibin.platform.arch.enums.ApproveStatus;
import com.aiguibin.platform.arch.enums.ExecuteStatus;
import com.aiguibin.platform.arch.mapper.DictItemChangeMapper;
import com.aiguibin.platform.arch.mapper.DictTypeChangeMapper;
import com.aiguibin.platform.arch.service.DictChangeApplyService;
import com.aiguibin.platform.arch.util.ChangeNoGenerator;
import com.aiguibin.platform.arch.util.DictChangeValidator;
import com.aiguibin.platform.arch.dto.DictItemImportVO;
import com.aiguibin.platform.arch.dto.DictTypeChangeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 变更申请服务实现
 */
@Service
@Slf4j
public class DictChangeApplyServiceImpl implements DictChangeApplyService {

    @Autowired
    private DictTypeChangeMapper changeMapper;

    @Autowired
    private DictItemChangeMapper itemChangeMapper;

    @Autowired
    private ChangeNoGenerator changeNoGenerator;

    @Autowired
    private DictChangeValidator validator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictTypeChangeVO applyChange(DictChangeApplyDTO dto) {
        // 1. 验证参数
        validator.validate(dto);

        // 2. 生成变更单号
        String changeNo = changeNoGenerator.generateChangeNo();

        // 3. 保存变更主记录
        DictTypeChange change = buildDictTypeChange(dto, changeNo, ApproveStatus.PENDING);
        changeMapper.insert(change);

        // 4. 保存字典项变更
        if (!dto.getItemChanges().isEmpty()) {
            List<DictItemChange> itemChanges = buildDictItemChanges(dto.getItemChanges(), change.getUuid(), change.getChangeNo(), change.getCreatedBy());
            itemChangeMapper.batchInsert(itemChanges);

            // 表结构无统计字段，仅触碰更新时间
            changeMapper.touchUpdatedTime(change.getUuid());
        }

        // 5. 记录操作日志
        logOperation(change, "提交变更申请");

        // 6. 发送通知
        sendApplyNotification(change);

        return convertToVO(change);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictTypeChangeVO saveDraft(DictChangeApplyDTO dto) {
        // #region agent log
        try {
            ObjectMapper mapper = new ObjectMapper();
            String logEntry = mapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("id", "log_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8));
                put("timestamp", System.currentTimeMillis());
                put("location", "DictChangeApplyServiceImpl.java:81");
                put("message", "saveDraft方法入口，验证前");
                put("data", new java.util.HashMap<String, Object>() {{
                    if (dto.getItemChanges() != null) {
                        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
                        for (int i = 0; i < dto.getItemChanges().size(); i++) {
                            final int index = i;
                            com.aiguibin.platform.arch.dto.DictItemChangeDTO item = dto.getItemChanges().get(i);
                            if (item.getNewData() != null) {
                                items.add(new java.util.HashMap<String, Object>() {{
                                    put("index", index);
                                    put("dctSeq", item.getNewData().getDctSeq());
                                    put("dctSeqIsNull", item.getNewData().getDctSeq() == null);
                                }});
                            }
                        }
                        put("newData_dctSeq_values", items);
                    }
                }});
                put("sessionId", "debug-session");
                put("runId", "run1");
                put("hypothesisId", "B");
            }});
            Files.write(Paths.get("e:\\WorkSpace\\HelloWorldBackend\\aiguibin-platform-arch\\.cursor\\debug.log"), 
                (logEntry + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        // 1. 验证参数
        validator.validate(dto);

        // 2. 生成变更单号
        String changeNo = changeNoGenerator.generateChangeNo();

        // 3. 保存变更主记录
        DictTypeChange change = buildDictTypeChange(dto, changeNo, ApproveStatus.DRAFT);
        changeMapper.insert(change);

        // 4. 保存字典项变更
        if (!dto.getItemChanges().isEmpty()) {
            List<DictItemChange> itemChanges = buildDictItemChanges(dto.getItemChanges(), change.getUuid(), change.getChangeNo(), change.getCreatedBy());
            itemChangeMapper.batchInsert(itemChanges);

            // 表结构无统计字段，仅触碰更新时间
            changeMapper.touchUpdatedTime(change.getUuid());
        }

        // 5. 记录操作日志
        logOperation(change, "保存草稿");

        return convertToVO(change);
    }

    @Override
    public DictItemImportVO importDictItems(MultipartFile file, String dictType) {
        // TODO: 实现导入逻辑
        return null;
    }

    private DictTypeChange buildDictTypeChange(DictChangeApplyDTO dto, String changeNo, ApproveStatus status) {
        DictTypeChange change = new DictTypeChange();
        String userNum = getCurrentUser();
        String userName = getCurrentUserName();
        change.setUuid(generate32BitUUID());
        change.setChangeNo(changeNo);
        change.setDctTpId(dto.getDictTypeId());
        change.setChangeType(dto.getChangeType().name());
        change.setOldDctTp(dto.getTypeChange().getOldDctTp());
        change.setNewDctTp(dto.getTypeChange().getNewDctTp());
        change.setOldDctTpNm(dto.getTypeChange().getOldDctTpNm());
        change.setNewDctTpNm(dto.getTypeChange().getNewDctTpNm());
        change.setChangeReason(dto.getChangeReason());
        change.setChangeImpact(dto.getChangeImpact());
        change.setApplyUserNum(userNum);
        change.setApplyUserName(userName);
        change.setApplyTime(DateUtil.date());
        change.setApproveStatus(status.name());
        change.setExecuteStatus(ExecuteStatus.PENDING.name());
        change.setCreatedBy(userNum);
        change.setCreatedTime(DateUtil.date());
        change.setUpdatedBy(userNum);
        change.setUpdatedTime(DateUtil.date());
        change.setIsDeleted(0);
        return change;
    }

    private List<DictItemChange> buildDictItemChanges(List<DictItemChangeDTO> itemDTOs, String changeId, String changeNo, String createdBy) {
        // #region agent log
        try {
            ObjectMapper mapper = new ObjectMapper();
            String logEntry = mapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("id", "log_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8));
                put("timestamp", System.currentTimeMillis());
                put("location", "DictChangeApplyServiceImpl.java:135");
                put("message", "buildDictItemChanges方法，处理newData前");
                put("data", new java.util.HashMap<String, Object>() {{
                    java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
                    for (int i = 0; i < itemDTOs.size(); i++) {
                        final int index = i;
                        com.aiguibin.platform.arch.dto.DictItemChangeDTO dto = itemDTOs.get(i);
                        if (dto.getNewData() != null) {
                            items.add(new java.util.HashMap<String, Object>() {{
                                put("index", index);
                                put("dctSeq_before", dto.getNewData().getDctSeq());
                                put("dctSeqIsNull", dto.getNewData().getDctSeq() == null);
                            }});
                        }
                    }
                    put("newData_dctSeq_before_processing", items);
                }});
                put("sessionId", "debug-session");
                put("runId", "run1");
                put("hypothesisId", "C");
            }});
            Files.write(Paths.get("e:\\WorkSpace\\HelloWorldBackend\\aiguibin-platform-arch\\.cursor\\debug.log"), 
                (logEntry + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        List<DictItemChange> items = new ArrayList<>();
        for (int i = 0; i < itemDTOs.size(); i++) {
            final int index = i;
            DictItemChangeDTO dto = itemDTOs.get(i);
            DictItemChange item = new DictItemChange();
            item.setUuid(generate32BitUUID());
            item.setChangeId(changeId);
            item.setChangeNo(changeNo);
            item.setCreatedBy(createdBy);
            item.setUpdatedBy(createdBy);
            item.setChangeOperation(dto.getChangeOperation());
            item.setItemOrder(i);
            
            if (dto.getOldData() != null) {
                item.setOldDctSeq(dto.getOldData().getDctSeq());
                item.setOldDctGrp(dto.getOldData().getDctGrp());
                item.setOldDctKey(dto.getOldData().getDctKey());
                item.setOldDctValNm(dto.getOldData().getDctValNm());
                item.setOldDctVal(dto.getOldData().getDctVal());
                item.setOldDctDsc(dto.getOldData().getDctDsc());
                item.setOldStcd(dto.getOldData().getStcd());
            }
            
            if (dto.getNewData() != null) {
                // #region agent log
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String logEntry = mapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                        put("id", "log_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8));
                        put("timestamp", System.currentTimeMillis());
                        put("location", "DictChangeApplyServiceImpl.java:156");
                        put("message", "设置newData的dctSeq值");
                        put("data", new java.util.HashMap<String, Object>() {{
                            put("index", index);
                            put("dctSeq_value", dto.getNewData().getDctSeq());
                            put("dctSeqIsNull", dto.getNewData().getDctSeq() == null);
                        }});
                        put("sessionId", "debug-session");
                        put("runId", "run1");
                        put("hypothesisId", "D");
                    }});
                    Files.write(Paths.get("e:\\WorkSpace\\HelloWorldBackend\\aiguibin-platform-arch\\.cursor\\debug.log"), 
                        (logEntry + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (Exception e) {}
                // #endregion
                item.setNewDctSeq(dto.getNewData().getDctSeq());
                item.setNewDctGrp(dto.getNewData().getDctGrp());
                item.setNewDctKey(dto.getNewData().getDctKey());
                item.setNewDctValNm(dto.getNewData().getDctValNm());
                item.setNewDctVal(dto.getNewData().getDctVal());
                item.setNewDctDsc(dto.getNewData().getDctDsc());
                item.setNewStcd(dto.getNewData().getStcd());
            }
            
            item.setExecuteStatus(ExecuteStatus.PENDING.name());
            item.setIsDeleted(0);
            items.add(item);
        }
        return items;
    }

    private void logOperation(DictTypeChange change, String operation) {
        // TODO: 实现操作日志记录
        log.info("{}: changeNo={}, applyUserNum={}", operation, change.getChangeNo(), change.getApplyUserNum());
    }

    private void sendApplyNotification(DictTypeChange change) {
        // TODO: 实现通知发送
        log.info("发送变更申请通知: changeNo={}", change.getChangeNo());
    }

    private DictTypeChangeVO convertToVO(DictTypeChange change) {
        DictTypeChangeVO vo = new DictTypeChangeVO();
        BeanUtils.copyProperties(change, vo);
        return vo;
    }

    private String getCurrentUser() {
        // TODO: 从上下文获取当前用户
        return "admin";
    }

    private String getCurrentUserName() {
        // TODO: 从上下文获取当前用户姓名
        return "管理员";
    }

    /**
     * 生成32位UUID（去掉连字符）
     * 对应MySQL表字段：uuid varchar(32)
     */
    private String generate32BitUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}

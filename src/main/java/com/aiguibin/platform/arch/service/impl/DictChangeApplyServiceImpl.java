package com.aiguibin.platform.arch.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
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

import java.util.ArrayList;
import java.util.List;

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
            List<DictItemChange> itemChanges = buildDictItemChanges(dto.getItemChanges(), change.getId());
            itemChangeMapper.batchInsert(itemChanges);

            // 更新统计信息
            updateChangeStatistics(change.getId());
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
        // 1. 验证参数
        validator.validate(dto);

        // 2. 生成变更单号
        String changeNo = changeNoGenerator.generateChangeNo();

        // 3. 保存变更主记录
        DictTypeChange change = buildDictTypeChange(dto, changeNo, ApproveStatus.DRAFT);
        changeMapper.insert(change);

        // 4. 保存字典项变更
        if (!dto.getItemChanges().isEmpty()) {
            List<DictItemChange> itemChanges = buildDictItemChanges(dto.getItemChanges(), change.getId());
            itemChangeMapper.batchInsert(itemChanges);

            // 更新统计信息
            updateChangeStatistics(change.getId());
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
        change.setId(IdUtil.fastUUID());
        change.setChangeNo(changeNo);
        change.setDctTpId(dto.getDictTypeId());
        change.setChangeType(dto.getChangeType().name());
        change.setOldDctTp(dto.getTypeChange().getOldDctTp());
        change.setNewDctTp(dto.getTypeChange().getNewDctTp());
        change.setOldDctTpNm(dto.getTypeChange().getOldDctTpNm());
        change.setNewDctTpNm(dto.getTypeChange().getNewDctTpNm());
        change.setChangeReason(dto.getChangeReason());
        change.setChangeImpact(dto.getChangeImpact());
        change.setApplyUser(getCurrentUser());
        change.setApplyTime(DateUtil.date());
        change.setApproveStatus(status.name());
        change.setExecuteStatus(ExecuteStatus.PENDING.name());
        change.setCreateTime(DateUtil.date());
        change.setUpdateTime(DateUtil.date());
        change.setIsDeleted(0);
        return change;
    }

    private List<DictItemChange> buildDictItemChanges(List<DictItemChangeDTO> itemDTOs, String changeId) {
        List<DictItemChange> items = new ArrayList<>();
        for (int i = 0; i < itemDTOs.size(); i++) {
            DictItemChangeDTO dto = itemDTOs.get(i);
            DictItemChange item = new DictItemChange();
            item.setId(IdUtil.fastUUID());
            item.setChangeId(changeId);
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

    private void updateChangeStatistics(String changeId) {
        List<DictItemChangeMapper.OperationCount> counts = itemChangeMapper.countByOperation(changeId);
        int addCount = 0, modCount = 0, delCount = 0;
        for (DictItemChangeMapper.OperationCount count : counts) {
            switch (count.getChangeOperation()) {
                case "ADD":
                    addCount = count.getCount();
                    break;
                case "MOD":
                    modCount = count.getCount();
                    break;
                case "DEL":
                    delCount = count.getCount();
                    break;
            }
        }
        changeMapper.updateChangeStatistics(changeId, addCount, modCount, delCount);
    }

    private void logOperation(DictTypeChange change, String operation) {
        // TODO: 实现操作日志记录
        log.info("{}: changeNo={}, applyUser={}", operation, change.getChangeNo(), change.getApplyUser());
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
}

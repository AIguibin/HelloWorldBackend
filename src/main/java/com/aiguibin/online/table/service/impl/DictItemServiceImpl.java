package com.aiguibin.online.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.online.table.entity.DictItem;
import com.aiguibin.online.table.mapper.DictItemMapper;
import com.aiguibin.online.table.service.DictItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典码值服务实现类
 */
@Service
public class DictItemServiceImpl implements DictItemService {
    
    @Resource
    private DictItemMapper dictItemMapper;
    
    @Override
    public Page<DictItem> page(int page, int size, String dictTypeCode, String dictValue, String dictLabel, String groupCode) {
        LambdaQueryWrapper<DictItem> qw = new LambdaQueryWrapper<>();
        qw.eq(DictItem::getIsDeleted, 0);
        
        if (dictTypeCode != null && !dictTypeCode.isEmpty()) {
            qw.eq(DictItem::getDictTypeCode, dictTypeCode);
        }
        
        if (dictValue != null && !dictValue.isEmpty()) {
            qw.like(DictItem::getDictValue, dictValue);
        }
        
        if (dictLabel != null && !dictLabel.isEmpty()) {
            qw.like(DictItem::getDictLabel, dictLabel);
        }
        
        if (groupCode != null && !groupCode.isEmpty()) {
            qw.eq(DictItem::getGroupCode, groupCode);
        }
        
        qw.orderByAsc(DictItem::getSort);
        return dictItemMapper.selectPage(new Page<>(page, size), qw);
    }
    
    @Override
    public List<DictItem> listByType(String dictTypeCode) {
        LambdaQueryWrapper<DictItem> qw = new LambdaQueryWrapper<>();
        qw.eq(DictItem::getIsDeleted, 0)
          .eq(DictItem::getIsEnabled, 1)
          .eq(DictItem::getDictTypeCode, dictTypeCode)
          .orderByAsc(DictItem::getSort);
        
        return dictItemMapper.selectList(qw);
    }
    
    @Override
    public List<DictItem> listByTypeAndGroup(String dictTypeCode, String groupCode) {
        LambdaQueryWrapper<DictItem> qw = new LambdaQueryWrapper<>();
        qw.eq(DictItem::getIsDeleted, 0)
          .eq(DictItem::getIsEnabled, 1)
          .eq(DictItem::getDictTypeCode, dictTypeCode);
        
        if (groupCode != null && !groupCode.isEmpty()) {
            qw.eq(DictItem::getGroupCode, groupCode);
        }
        
        qw.orderByAsc(DictItem::getSort);
        return dictItemMapper.selectList(qw);
    }
    
    @Override
    public DictItem getById(Long id) {
        return dictItemMapper.selectById(id);
    }
    
    @Override
    public DictItem getByTypeAndValue(String dictTypeCode, String dictValue) {
        LambdaQueryWrapper<DictItem> qw = new LambdaQueryWrapper<>();
        qw.eq(DictItem::getIsDeleted, 0)
          .eq(DictItem::getIsEnabled, 1)
          .eq(DictItem::getDictTypeCode, dictTypeCode)
          .eq(DictItem::getDictValue, dictValue);
        
        return dictItemMapper.selectOne(qw);
    }
    
    @Override
    public Long create(DictItem dictItem, String operator) {
        dictItem.setCreateUser(operator);
        dictItem.setUpdateUser(operator);
        dictItem.setCreateTime(LocalDateTime.now());
        dictItem.setUpdateTime(LocalDateTime.now());
        dictItem.setIsDeleted(0);
        
        if (dictItem.getIsEnabled() == null) {
            dictItem.setIsEnabled(1);
        }
        
        if (dictItem.getSort() == null) {
            dictItem.setSort(0);
        }
        
        dictItemMapper.insert(dictItem);
        return dictItem.getId();
    }
    
    @Override
    public boolean update(Long id, DictItem dictItem, String operator) {
        LambdaUpdateWrapper<DictItem> uw = new LambdaUpdateWrapper<>();
        uw.eq(DictItem::getId, id)
          .eq(DictItem::getIsDeleted, 0);
        
        uw.set(DictItem::getDictLabel, dictItem.getDictLabel())
          .set(DictItem::getGroupCode, dictItem.getGroupCode())
          .set(DictItem::getGroupName, dictItem.getGroupName())
          .set(DictItem::getSort, dictItem.getSort())
          .set(DictItem::getIsEnabled, dictItem.getIsEnabled())
          .set(DictItem::getDescription, dictItem.getDescription())
          .set(DictItem::getUpdateUser, operator)
          .set(DictItem::getUpdateTime, LocalDateTime.now());
        
        return dictItemMapper.update(null, uw) > 0;
    }
    
    @Override
    public boolean delete(Long id, String operator) {
        LambdaUpdateWrapper<DictItem> uw = new LambdaUpdateWrapper<>();
        uw.eq(DictItem::getId, id)
          .eq(DictItem::getIsDeleted, 0);
        
        uw.set(DictItem::getIsDeleted, 1)
          .set(DictItem::getUpdateUser, operator)
          .set(DictItem::getUpdateTime, LocalDateTime.now());
        
        return dictItemMapper.update(null, uw) > 0;
    }
}
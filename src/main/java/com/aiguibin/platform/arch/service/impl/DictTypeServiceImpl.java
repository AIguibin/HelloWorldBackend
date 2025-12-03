package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.DictType;
import com.aiguibin.platform.arch.mapper.DictTypeMapper;
import com.aiguibin.platform.arch.service.DictTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典类型服务实现类
 */
@Service
public class DictTypeServiceImpl implements DictTypeService {
    
    @Resource
    private DictTypeMapper dictTypeMapper;
    
    @Override
    public Page<DictType> page(int page, int size, String dictTypeCode, String dictTypeName) {
        LambdaQueryWrapper<DictType> qw = new LambdaQueryWrapper<>();
        qw.eq(DictType::getIsDeleted, 0);
        
        if (dictTypeCode != null && !dictTypeCode.isEmpty()) {
            qw.like(DictType::getDictTypeCode, dictTypeCode);
        }
        
        if (dictTypeName != null && !dictTypeName.isEmpty()) {
            qw.like(DictType::getDictTypeName, dictTypeName);
        }
        
        qw.orderByAsc(DictType::getSortOrder);
        return dictTypeMapper.selectPage(new Page<>(page, size), qw);
    }
    
    @Override
    public List<DictType> listEnabled() {
        LambdaQueryWrapper<DictType> qw = new LambdaQueryWrapper<>();
        qw.eq(DictType::getIsDeleted, 0)
          .eq(DictType::getStatus, 1)
          .orderByAsc(DictType::getSortOrder);
        
        return dictTypeMapper.selectList(qw);
    }
    
    @Override
    public DictType getById(Long id) {
        return dictTypeMapper.selectById(id);
    }
    
    @Override
    public DictType getByCode(String dictTypeCode) {
        LambdaQueryWrapper<DictType> qw = new LambdaQueryWrapper<>();
        qw.eq(DictType::getIsDeleted, 0)
          .eq(DictType::getDictTypeCode, dictTypeCode);
        
        return dictTypeMapper.selectOne(qw);
    }
    
    @Override
    public Long create(DictType dictType, String operator) {
        dictType.setCreateUser(operator);
        dictType.setUpdateUser(operator);
        dictType.setCreateTime(LocalDateTime.now());
        dictType.setUpdateTime(LocalDateTime.now());
        dictType.setIsDeleted(0);
        
        if (dictType.getStatus() == null) {
            dictType.setStatus(1);
        }
        
        if (dictType.getSortOrder() == null) {
            dictType.setSortOrder(0);
        }
        
        dictTypeMapper.insert(dictType);
        return dictType.getId();
    }
    
    @Override
    public boolean update(Long id, DictType dictType, String operator) {
        LambdaUpdateWrapper<DictType> uw = new LambdaUpdateWrapper<>();
        uw.eq(DictType::getId, id)
          .eq(DictType::getIsDeleted, 0);
        
        uw.set(DictType::getDictTypeName, dictType.getDictTypeName())
          .set(DictType::getDescription, dictType.getDescription())
          .set(DictType::getSortOrder, dictType.getSortOrder())
          .set(DictType::getStatus, dictType.getStatus())
          .set(DictType::getUpdateUser, operator)
          .set(DictType::getUpdateTime, LocalDateTime.now());
        
        return dictTypeMapper.update(null, uw) > 0;
    }
    
    @Override
    public boolean delete(Long id, String operator) {
        LambdaUpdateWrapper<DictType> uw = new LambdaUpdateWrapper<>();
        uw.eq(DictType::getId, id)
          .eq(DictType::getIsDeleted, 0);
        
        uw.set(DictType::getIsDeleted, 1)
          .set(DictType::getUpdateUser, operator)
          .set(DictType::getUpdateTime, LocalDateTime.now());
        
        return dictTypeMapper.update(null, uw) > 0;
    }
}
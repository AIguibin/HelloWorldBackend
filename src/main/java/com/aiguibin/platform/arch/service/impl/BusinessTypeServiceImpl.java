package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.BusinessType;
import com.aiguibin.platform.arch.mapper.BusinessTypeMapper;
import com.aiguibin.platform.arch.service.BusinessTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务类型Service实现类
 * 实现业务类型的查询、新增、修改、删除等功能
 */
@Service
public class BusinessTypeServiceImpl implements BusinessTypeService {

    @Resource
    private BusinessTypeMapper businessTypeMapper;

    @Override
    public List<BusinessType> getAllBusinessTypes() {
        LambdaQueryWrapper<BusinessType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusinessType::getIsActive, 1)
                .eq(BusinessType::getIsDeleted, 0)
                .orderByAsc(BusinessType::getTypeName);
        return businessTypeMapper.selectList(queryWrapper);
    }

    @Override
    public BusinessType getBusinessTypeByCode(String typeCode) {
        LambdaQueryWrapper<BusinessType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusinessType::getTypeCode, typeCode)
                .eq(BusinessType::getIsActive, 1)
                .eq(BusinessType::getIsDeleted, 0);
        return businessTypeMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<BusinessType> getBusinessTypesPage(int page, int size) {
        LambdaQueryWrapper<BusinessType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusinessType::getIsDeleted, 0)
                .orderByAsc(BusinessType::getTypeName);
        return businessTypeMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public boolean addBusinessType(BusinessType businessType) {
        // 验证必填字段
        if (businessType.getTypeCode() == null || businessType.getTypeCode().isEmpty() ||
                businessType.getTypeName() == null || businessType.getTypeName().isEmpty() ||
                businessType.getMainTableName() == null || businessType.getMainTableName().isEmpty()) {
            return false;
        }

        // 设置默认值
        if (businessType.getIsActive() == null) {
            businessType.setIsActive(1);
        }
        if (businessType.getIsDeleted() == null) {
            businessType.setIsDeleted(0);
        }
        if (businessType.getIdFieldName() == null || businessType.getIdFieldName().isEmpty()) {
            businessType.setIdFieldName("id");
        }
        if (businessType.getStatusFieldName() == null || businessType.getStatusFieldName().isEmpty()) {
            businessType.setStatusFieldName("current_status");
        }

        return businessTypeMapper.insert(businessType) > 0;
    }

    @Override
    public boolean updateBusinessType(BusinessType businessType) {
        // 验证必填字段
        if (businessType.getId() == null || businessType.getId() <= 0) {
            return false;
        }

        return businessTypeMapper.updateById(businessType) > 0;
    }

    @Override
    public boolean deleteBusinessType(Long id) {
        // 逻辑删除
        BusinessType businessType = new BusinessType();
        businessType.setId(id);
        businessType.setIsDeleted(1);
        return businessTypeMapper.updateById(businessType) > 0;
    }
}

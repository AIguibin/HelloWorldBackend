package com.aiguibin.platform.arch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.BusinessType;

import java.util.List;

/**
 * 业务类型Service接口
 * 提供业务类型的查询、新增、修改、删除等功能
 */
public interface BusinessTypeService {

    /**
     * 查询所有业务类型
     * @return 业务类型列表
     */
    List<BusinessType> getAllBusinessTypes();

    /**
     * 根据业务类型编码查询业务类型
     * @param typeCode 业务类型编码
     * @return 业务类型对象
     */
    BusinessType getBusinessTypeByCode(String typeCode);

    /**
     * 分页查询业务类型
     * @param page 页码
     * @param size 每页条数
     * @return 分页业务类型列表
     */
    Page<BusinessType> getBusinessTypesPage(int page, int size);

    /**
     * 新增业务类型
     * @param businessType 业务类型对象
     * @return 是否成功
     */
    boolean addBusinessType(BusinessType businessType);

    /**
     * 修改业务类型
     * @param businessType 业务类型对象
     * @return 是否成功
     */
    boolean updateBusinessType(BusinessType businessType);

    /**
     * 删除业务类型
     * @param id 业务类型ID
     * @return 是否成功
     */
    boolean deleteBusinessType(Long id);
}

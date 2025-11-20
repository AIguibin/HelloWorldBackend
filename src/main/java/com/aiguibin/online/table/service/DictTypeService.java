package com.aiguibin.online.table.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.online.table.entity.DictType;

import java.util.List;

/**
 * 字典类型服务
 */
public interface DictTypeService {
    
    /**
     * 分页查询字典类型
     * @param page 页码
     * @param size 每页大小
     * @param dictTypeCode 字典类型编码
     * @param dictTypeName 字典类型名称
     * @return 分页结果
     */
    Page<DictType> page(int page, int size, String dictTypeCode, String dictTypeName);
    
    /**
     * 查询所有启用的字典类型
     * @return 字典类型列表
     */
    List<DictType> listEnabled();
    
    /**
     * 根据ID查询字典类型
     * @param id 字典类型ID
     * @return 字典类型
     */
    DictType getById(Long id);
    
    /**
     * 根据编码查询字典类型
     * @param dictTypeCode 字典类型编码
     * @return 字典类型
     */
    DictType getByCode(String dictTypeCode);
    
    /**
     * 创建字典类型
     * @param dictType 字典类型
     * @param operator 操作人
     * @return 字典类型ID
     */
    Long create(DictType dictType, String operator);
    
    /**
     * 更新字典类型
     * @param id 字典类型ID
     * @param dictType 字典类型
     * @param operator 操作人
     * @return 是否成功
     */
    boolean update(Long id, DictType dictType, String operator);
    
    /**
     * 删除字典类型
     * @param id 字典类型ID
     * @param operator 操作人
     * @return 是否成功
     */
    boolean delete(Long id, String operator);
}
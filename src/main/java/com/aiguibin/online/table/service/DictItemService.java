package com.aiguibin.online.table.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.online.table.entity.DictItem;

import java.util.List;

/**
 * 字典码值服务
 */
public interface DictItemService {
    
    /**
     * 分页查询字典码值
     * @param page 页码
     * @param size 每页大小
     * @param dictTypeCode 字典类型编码
     * @param dictValue 字典值
     * @param dictLabel 字典标签
     * @param groupCode 分组编码
     * @return 分页结果
     */
    Page<DictItem> page(int page, int size, String dictTypeCode, String dictValue, String dictLabel, String groupCode);
    
    /**
     * 根据字典类型编码查询字典码值
     * @param dictTypeCode 字典类型编码
     * @return 字典码值列表
     */
    List<DictItem> listByType(String dictTypeCode);
    
    /**
     * 根据字典类型编码和分组编码查询字典码值
     * @param dictTypeCode 字典类型编码
     * @param groupCode 分组编码（可为空，表示查询所有分组）
     * @return 字典码值列表
     */
    List<DictItem> listByTypeAndGroup(String dictTypeCode, String groupCode);
    
    /**
     * 根据ID查询字典码值
     * @param id 字典码值ID
     * @return 字典码值
     */
    DictItem getById(Long id);
    
    /**
     * 创建字典码值
     * @param dictItem 字典码值
     * @param operator 操作人
     * @return 字典码值ID
     */
    Long create(DictItem dictItem, String operator);
    
    /**
     * 更新字典码值
     * @param id 字典码值ID
     * @param dictItem 字典码值
     * @param operator 操作人
     * @return 是否成功
     */
    boolean update(Long id, DictItem dictItem, String operator);
    
    /**
     * 删除字典码值
     * @param id 字典码值ID
     * @param operator 操作人
     * @return 是否成功
     */
    boolean delete(Long id, String operator);
    
    /**
     * 根据字典类型编码和字典值查询字典码值
     * @param dictTypeCode 字典类型编码
     * @param dictValue 字典值
     * @return 字典码值
     */
    DictItem getByTypeAndValue(String dictTypeCode, String dictValue);
}
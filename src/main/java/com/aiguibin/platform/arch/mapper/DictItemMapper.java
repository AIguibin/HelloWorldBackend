package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.DictItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典码值Mapper
 */
public interface DictItemMapper extends BaseMapper<DictItem> {
    
    /**
     * 根据字典类型编码和分组编码查询字典项
     * @param dictTypeCode 字典类型编码
     * @param groupCode 分组编码（可为空，表示查询所有分组）
     * @return 字典项列表
     */
    List<DictItem> selectByTypeAndGroup(@Param("dictTypeCode") String dictTypeCode, @Param("groupCode") String groupCode);
    
    /**
     * 根据字典类型编码查询所有启用的字典项
     * @param dictTypeCode 字典类型编码
     * @return 字典项列表
     */
    List<DictItem> selectEnabledByType(@Param("dictTypeCode") String dictTypeCode);
}
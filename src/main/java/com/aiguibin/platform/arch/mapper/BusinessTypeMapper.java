package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.BusinessType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 业务类型Mapper接口
 * 对应biz_business_type表的CRUD操作
 */
@Mapper
public interface BusinessTypeMapper extends BaseMapper<BusinessType> {
}

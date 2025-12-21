package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysApiResource;
import org.apache.ibatis.annotations.Mapper;

/**
 * API资源定义表Mapper接口
 * 对应sys_api_resource表，用于操作API资源定义数据
 */
@Mapper
public interface SysApiResourceMapper extends BaseMapper<SysApiResource> {
}
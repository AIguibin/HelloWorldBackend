package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysPermResource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限-资源关联表Mapper接口
 * 对应sys_perm_resource表，用于操作权限与资源的关联关系
 */
@Mapper
public interface SysPermResourceMapper extends BaseMapper<SysPermResource> {
}
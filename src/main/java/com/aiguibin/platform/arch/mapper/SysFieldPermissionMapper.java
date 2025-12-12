package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysFieldPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字段权限表Mapper接口
 * 提供sys_field_permission表的数据库访问方法
 */
@Mapper
public interface SysFieldPermissionMapper extends BaseMapper<SysFieldPermission> {
    
    // BaseMapper已经提供了基本的CRUD方法，如需扩展可在此添加自定义方法
}
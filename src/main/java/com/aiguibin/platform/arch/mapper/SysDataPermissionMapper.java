package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysDataPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据权限子表Mapper接口
 * 对应sys_data_permission表，用于操作数据权限规则
 */
@Mapper
public interface SysDataPermissionMapper extends BaseMapper<SysDataPermission> {
}
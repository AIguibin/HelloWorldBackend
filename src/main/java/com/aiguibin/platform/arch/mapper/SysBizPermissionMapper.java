package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysBizPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 业务权限子表Mapper接口
 * 对应sys_biz_permission表，用于操作业务权限规则
 */
@Mapper
public interface SysBizPermissionMapper extends BaseMapper<SysBizPermission> {
}
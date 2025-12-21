package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguibin.platform.arch.entity.SysDataPermission;
import com.aiguibin.platform.arch.mapper.SysDataPermissionMapper;
import com.aiguibin.platform.arch.service.SysDataPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据权限子表Service实现类
 * 对应sys_data_permission表，用于操作数据权限规则
 */
@Service
public class SysDataPermissionServiceImpl extends ServiceImpl<SysDataPermissionMapper, SysDataPermission> implements SysDataPermissionService {

    @Autowired
    private SysDataPermissionMapper sysDataPermissionMapper;
}

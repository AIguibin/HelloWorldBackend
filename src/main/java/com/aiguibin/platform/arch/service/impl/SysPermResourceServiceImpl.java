package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguibin.platform.arch.entity.SysPermResource;
import com.aiguibin.platform.arch.mapper.SysPermResourceMapper;
import com.aiguibin.platform.arch.service.SysPermResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限-资源关联表Service实现类
 * 对应sys_perm_resource表，用于操作权限与资源的关联关系
 */
@Service
public class SysPermResourceServiceImpl extends ServiceImpl<SysPermResourceMapper, SysPermResource> implements SysPermResourceService {

    @Autowired
    private SysPermResourceMapper sysPermResourceMapper;
}

package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguibin.platform.arch.entity.SysApiResource;
import com.aiguibin.platform.arch.mapper.SysApiResourceMapper;
import com.aiguibin.platform.arch.service.SysApiResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * API资源定义表Service实现类
 * 对应sys_api_resource表，用于操作API资源定义数据
 */
@Service
public class SysApiResourceServiceImpl extends ServiceImpl<SysApiResourceMapper, SysApiResource> implements SysApiResourceService {

    @Autowired
    private SysApiResourceMapper sysApiResourceMapper;
}

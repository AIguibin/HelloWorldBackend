package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.entity.NetExternalLink;
import com.aiguibin.platform.arch.mapper.NetExternalLinkMapper;
import com.aiguibin.platform.arch.service.NetExternalLinkService;
import com.aiguibin.platform.arch.util.LinkCodeGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 信贷系统外部链接网络管理清单服务实现类
 */
@Service
public class NetExternalLinkServiceImpl implements NetExternalLinkService {
    
    @Resource
    private NetExternalLinkMapper netExternalLinkMapper;
    
    @Resource
    private LinkCodeGenerator linkCodeGenerator;
    
    /**
     * 最大导出数量限制
     */
    private static final int MAX_EXPORT_SIZE = 10000;
    
    @Override
    public Page<NetExternalLink> page(int page, int size, String sourceEnv, String targetEnv, 
                                     String status, String protocol, String keyword) {
        LambdaQueryWrapper<NetExternalLink> qw = new LambdaQueryWrapper<>();
        
        // 过滤已删除数据
        qw.eq(NetExternalLink::getIsDeleted, 0);
        
        // 源环境筛选
        if (sourceEnv != null && !sourceEnv.isEmpty() && !"all".equals(sourceEnv)) {
            qw.eq(NetExternalLink::getSourceEnv, sourceEnv);
        }
        
        // 目标环境筛选
        if (targetEnv != null && !targetEnv.isEmpty() && !"all".equals(targetEnv)) {
            qw.eq(NetExternalLink::getTargetEnv, targetEnv);
        }
        
        // 状态筛选
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            qw.eq(NetExternalLink::getStatus, status);
        }
        
        // 协议筛选
        if (protocol != null && !protocol.isEmpty() && !"all".equals(protocol)) {
            qw.eq(NetExternalLink::getProtocol, protocol);
        }
        
        // 关键字搜索（链路编号、链路名称、源IP、目标主机、使用场景、描述）
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(wrapper -> wrapper
                .like(NetExternalLink::getLinkCode, keyword)
                .or().like(NetExternalLink::getLinkName, keyword)
                .or().like(NetExternalLink::getSourceIp, keyword)
                .or().like(NetExternalLink::getTargetHost, keyword)
                .or().like(NetExternalLink::getScenario, keyword)
                .or().like(NetExternalLink::getDescription, keyword)
            );
        }
        
        // 按更新时间倒序排序
        qw.orderByDesc(NetExternalLink::getUpdatedTime);
        
        return netExternalLinkMapper.selectPage(new Page<>(page, size), qw);
    }
    
    @Override
    public NetExternalLink getById(Long id) {
        LambdaQueryWrapper<NetExternalLink> qw = new LambdaQueryWrapper<>();
        qw.eq(NetExternalLink::getId, id)
          .eq(NetExternalLink::getIsDeleted, 0);
        return netExternalLinkMapper.selectOne(qw);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(NetExternalLink link, String operator) {
        // 生成UUID
        link.setUuid(UUID.randomUUID().toString().replace("-", ""));
        
        // 自动生成链路编号（如果未提供）
        if (link.getLinkCode() == null || link.getLinkCode().isEmpty()) {
            // 从目标系统名称提取简写（简化处理，取前3个字符的大写）
            String systemAbbr = link.getTargetSystem() != null && link.getTargetSystem().length() >= 3
                ? link.getTargetSystem().substring(0, 3).toUpperCase().replaceAll("[^A-Z]", "")
                : "SYS";
            link.setLinkCode(linkCodeGenerator.generateLinkCode(systemAbbr));
        }
        
        // 检查链路编号唯一性
        LambdaQueryWrapper<NetExternalLink> qw = new LambdaQueryWrapper<>();
        qw.eq(NetExternalLink::getLinkCode, link.getLinkCode())
          .eq(NetExternalLink::getIsDeleted, 0);
        Long count = netExternalLinkMapper.selectCount(qw);
        if (count > 0) {
            throw new RuntimeException("链路编号已存在：" + link.getLinkCode());
        }
        
        // 设置默认值
        if (link.getStatus() == null || link.getStatus().isEmpty()) {
            link.setStatus("active");
        }
        link.setIsDeleted(0);
        link.setCreatedBy(operator);
        link.setUpdatedBy(operator);
        
        netExternalLinkMapper.insert(link);
        return link.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Long id, NetExternalLink link, String operator) {
        // 检查记录是否存在且未删除
        NetExternalLink existing = getById(id);
        if (existing == null) {
            throw new RuntimeException("链路不存在或已删除");
        }
        
        // 检查链路编号唯一性（排除自身）
        if (link.getLinkCode() != null && !link.getLinkCode().equals(existing.getLinkCode())) {
            LambdaQueryWrapper<NetExternalLink> qw = new LambdaQueryWrapper<>();
            qw.eq(NetExternalLink::getLinkCode, link.getLinkCode())
              .ne(NetExternalLink::getId, id)
              .eq(NetExternalLink::getIsDeleted, 0);
            Long count = netExternalLinkMapper.selectCount(qw);
            if (count > 0) {
                throw new RuntimeException("链路编号已存在：" + link.getLinkCode());
            }
        }
        
        // 更新字段
        link.setId(id);
        link.setUuid(existing.getUuid()); // 保持UUID不变
        link.setUpdatedBy(operator);
        
        LambdaUpdateWrapper<NetExternalLink> uw = new LambdaUpdateWrapper<>();
        uw.eq(NetExternalLink::getId, id)
          .eq(NetExternalLink::getIsDeleted, 0);
        
        // 动态更新非空字段
        if (link.getLinkCode() != null) uw.set(NetExternalLink::getLinkCode, link.getLinkCode());
        if (link.getLinkName() != null) uw.set(NetExternalLink::getLinkName, link.getLinkName());
        if (link.getSourceSystem() != null) uw.set(NetExternalLink::getSourceSystem, link.getSourceSystem());
        if (link.getSourceEnv() != null) uw.set(NetExternalLink::getSourceEnv, link.getSourceEnv());
        if (link.getSourceIp() != null) uw.set(NetExternalLink::getSourceIp, link.getSourceIp());
        if (link.getTargetSystem() != null) uw.set(NetExternalLink::getTargetSystem, link.getTargetSystem());
        if (link.getTargetEnv() != null) uw.set(NetExternalLink::getTargetEnv, link.getTargetEnv());
        if (link.getTargetHost() != null) uw.set(NetExternalLink::getTargetHost, link.getTargetHost());
        if (link.getTargetPort() != null) uw.set(NetExternalLink::getTargetPort, link.getTargetPort());
        if (link.getProtocol() != null) uw.set(NetExternalLink::getProtocol, link.getProtocol());
        if (link.getAuthMethod() != null) uw.set(NetExternalLink::getAuthMethod, link.getAuthMethod());
        if (link.getScenario() != null) uw.set(NetExternalLink::getScenario, link.getScenario());
        if (link.getDescription() != null) uw.set(NetExternalLink::getDescription, link.getDescription());
        if (link.getOwner() != null) uw.set(NetExternalLink::getOwner, link.getOwner());
        if (link.getStatus() != null) uw.set(NetExternalLink::getStatus, link.getStatus());
        if (link.getMonitoringLevel() != null) uw.set(NetExternalLink::getMonitoringLevel, link.getMonitoringLevel());
        uw.set(NetExternalLink::getUpdatedBy, operator);
        
        return netExternalLinkMapper.update(null, uw) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id, String operator) {
        LambdaUpdateWrapper<NetExternalLink> uw = new LambdaUpdateWrapper<>();
        uw.eq(NetExternalLink::getId, id)
          .eq(NetExternalLink::getIsDeleted, 0)
          .set(NetExternalLink::getIsDeleted, 1)
          .set(NetExternalLink::getUpdatedBy, operator);
        
        return netExternalLinkMapper.update(null, uw) > 0;
    }
    
    @Override
    public List<NetExternalLink> listForExport(String sourceEnv, String targetEnv, 
                                              String status, String protocol, String keyword) {
        LambdaQueryWrapper<NetExternalLink> qw = new LambdaQueryWrapper<>();
        
        // 过滤已删除数据
        qw.eq(NetExternalLink::getIsDeleted, 0);
        
        // 源环境筛选
        if (sourceEnv != null && !sourceEnv.isEmpty() && !"all".equals(sourceEnv)) {
            qw.eq(NetExternalLink::getSourceEnv, sourceEnv);
        }
        
        // 目标环境筛选
        if (targetEnv != null && !targetEnv.isEmpty() && !"all".equals(targetEnv)) {
            qw.eq(NetExternalLink::getTargetEnv, targetEnv);
        }
        
        // 状态筛选
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            qw.eq(NetExternalLink::getStatus, status);
        }
        
        // 协议筛选
        if (protocol != null && !protocol.isEmpty() && !"all".equals(protocol)) {
            qw.eq(NetExternalLink::getProtocol, protocol);
        }
        
        // 关键字搜索
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(wrapper -> wrapper
                .like(NetExternalLink::getLinkCode, keyword)
                .or().like(NetExternalLink::getLinkName, keyword)
                .or().like(NetExternalLink::getSourceIp, keyword)
                .or().like(NetExternalLink::getTargetHost, keyword)
                .or().like(NetExternalLink::getScenario, keyword)
                .or().like(NetExternalLink::getDescription, keyword)
            );
        }
        
        // 按更新时间倒序排序
        qw.orderByDesc(NetExternalLink::getUpdatedTime);
        
        // 限制最大导出数量
        qw.last("LIMIT " + MAX_EXPORT_SIZE);
        
        return netExternalLinkMapper.selectList(qw);
    }
}

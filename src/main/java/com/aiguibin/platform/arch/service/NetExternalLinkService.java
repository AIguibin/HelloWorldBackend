package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.entity.NetExternalLink;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 信贷系统外部链接网络管理清单服务接口
 */
public interface NetExternalLinkService {
    
    /**
     * 分页查询链路列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sourceEnv 源环境
     * @param targetEnv 目标环境
     * @param status 状态
     * @param protocol 协议
     * @param keyword 关键字（搜索链路编号、名称、IP、场景等）
     * @return 分页结果
     */
    Page<NetExternalLink> page(int page, int size, String sourceEnv, String targetEnv, 
                               String status, String protocol, String keyword);
    
    /**
     * 根据ID查询链路详情
     * 
     * @param id 链路ID
     * @return 链路信息
     */
    NetExternalLink getById(Long id);
    
    /**
     * 创建链路
     * 
     * @param link 链路信息
     * @param operator 操作人
     * @return 链路ID
     */
    Long create(NetExternalLink link, String operator);
    
    /**
     * 更新链路
     * 
     * @param id 链路ID
     * @param link 链路信息
     * @param operator 操作人
     * @return 是否成功
     */
    boolean update(Long id, NetExternalLink link, String operator);
    
    /**
     * 删除链路（逻辑删除）
     * 
     * @param id 链路ID
     * @param operator 操作人
     * @return 是否成功
     */
    boolean delete(Long id, String operator);
    
    /**
     * 查询导出数据（不分页，返回全部符合条件的记录）
     * 
     * @param sourceEnv 源环境
     * @param targetEnv 目标环境
     * @param status 状态
     * @param protocol 协议
     * @param keyword 关键字
     * @return 链路列表
     */
    List<NetExternalLink> listForExport(String sourceEnv, String targetEnv, 
                                         String status, String protocol, String keyword);
}

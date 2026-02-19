package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.NetExternalLink;
import com.aiguibin.platform.arch.dto.ResultVO;
import com.aiguibin.platform.arch.service.NetExternalLinkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 信贷系统外部链接网络管理清单控制器
 */
@RestController
@RequestMapping("/api/net-links")
public class NetExternalLinkController {
    
    @Resource
    private NetExternalLinkService netExternalLinkService;
    
    /**
     * 分页查询链路列表
     */
    @GetMapping
    public ResultVO<Page<NetExternalLink>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sourceEnv,
            @RequestParam(required = false) String targetEnv,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String protocol,
            @RequestParam(required = false) String keyword) {
        Page<NetExternalLink> pageData = netExternalLinkService.page(
            page, size, sourceEnv, targetEnv, status, protocol, keyword);
        return ResultVO.success(pageData);
    }
    
    /**
     * 根据ID查询链路详情
     */
    @GetMapping("/{id}")
    public ResultVO<NetExternalLink> getById(@PathVariable Long id) {
        NetExternalLink link = netExternalLinkService.getById(id);
        if (link == null) {
            return ResultVO.error("链路不存在或已删除");
        }
        return ResultVO.success(link);
    }
    
    /**
     * 创建链路
     */
    @PostMapping
    public ResultVO<Long> create(@RequestBody @Validated NetExternalLink link, 
                                    HttpServletRequest request) {
        String operator = getOperatorFromRequest(request);
        try {
            Long id = netExternalLinkService.create(link, operator);
            return ResultVO.success(id);
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }
    
    /**
     * 更新链路
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> update(@PathVariable Long id, 
                                       @RequestBody @Validated NetExternalLink link,
                                       HttpServletRequest request) {
        String operator = getOperatorFromRequest(request);
        try {
            boolean ok = netExternalLinkService.update(id, link, operator);
            return ResultVO.success(ok);
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }
    
    /**
     * 删除链路（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> delete(@PathVariable Long id, HttpServletRequest request) {
        String operator = getOperatorFromRequest(request);
        try {
            boolean ok = netExternalLinkService.delete(id, operator);
            return ResultVO.success(ok);
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }
    
    /**
     * 从请求中获取操作人（用户编号）
     * operator格式：userNum|userName，这里只需要userNum
     */
    private String getOperatorFromRequest(HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        if (operator != null && operator.contains("|")) {
            return operator.split("\\|")[0];
        }
        return operator != null ? operator : "system";
    }
}
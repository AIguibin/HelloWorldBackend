package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.service.AuthService;
import com.aiguibin.platform.arch.service.TokenService;
import com.aiguibin.platform.arch.service.PermissionCheckService;
import com.aiguibin.platform.arch.service.UserOrgDeptService;
import com.aiguibin.platform.arch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 认证服务实现类（入口类）
 * 负责接收并处理传入的userNum和orgCode参数进行组织，作为权限检查的入口
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private PermissionCheckService permissionCheckService;
    
    @Autowired
    private UserOrgDeptService userOrgDeptService;

    @Override
    public String issueToken(String userNum) {
        return tokenService.issueToken(userNum);
    }

    @Override
    public String getUserNumByToken(String token) {
        return tokenService.getUserNumByToken(token);
    }

    @Override
    public String getCsrfToken(String token) {
        return tokenService.getCsrfToken(token);
    }

    @Override
    public boolean validateCsrf(String token, String csrfToken) {
        return tokenService.validateCsrf(token, csrfToken);
    }

    @Override
    public boolean invalidate(String token) {
        return tokenService.invalidate(token);
    }

    @Override
    public String issueToken(String userNum, String orgCode) {
        return tokenService.issueToken(userNum, orgCode);
    }

    @Override
    public String getUserNumFromToken(String token) {
        return tokenService.getUserNumFromToken(token);
    }

    @Override
    public List<Map<String, Object>> getUserAllOrgDeptInfo(User user) {
        return userOrgDeptService.getUserAllOrgDeptInfo(user);
    }

    @Override
    public Map<String, Object> checkOrgAccess(String userNum, String orgCode) {
        return permissionCheckService.checkOrgAccess(userNum, orgCode);
    }

    @Override
    public String generateRefreshToken(String userNum, String orgCode) {
        return tokenService.generateRefreshToken(userNum, orgCode);
    }
}
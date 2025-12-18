package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.dto.UserOrgDeptVO;
import com.aiguibin.platform.arch.entity.User;

import java.util.List;
import java.util.Map;

public interface AuthService {
    String issueToken(String userNum);
    String issueToken(String userNum, String orgCode);
    String getUserNumByToken(String token);
    String getUserNumFromToken(String token);
    String getCsrfToken(String token);
    boolean validateCsrf(String token, String csrfToken);
    boolean invalidate(String token);
    
    /**
     * 获取用户机构部门信息
     * @param userNum 用户编号
     * @return 用户机构部门信息列表
     */
    List<Map<String, Object>> getUserOrgDeptInfo(User user);
    
    /**
     * 验证用户在指定机构下的访问权限
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 包含用户完整权限信息的Map
     */
    Map<String, Object> checkOrgAccess(String userNum, String orgCode);
    
    /**
     * 生成刷新token
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 刷新token
     */
    String generateRefreshToken(String userNum, String orgCode);
}
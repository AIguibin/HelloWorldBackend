package com.aiguibin.platform.arch.service;

/**
 * Token管理服务接口
 * 负责Token的生成、验证、失效处理等
 */
public interface TokenService {
    /**
     * 生成访问Token
     * @param userNum 用户编号
     * @return 访问Token
     */
    String issueToken(String userNum);
    
    /**
     * 生成带机构信息的访问Token
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 访问Token
     */
    String issueToken(String userNum, String orgCode);
    
    /**
     * 通过Token获取用户编号
     * @param token 访问Token
     * @return 用户编号
     */
    String getUserNumByToken(String token);
    
    /**
     * 从Token中提取用户编号（兼容带机构信息的Token）
     * @param token 访问Token
     * @return 用户编号
     */
    String getUserNumFromToken(String token);
    
    /**
     * 获取CSRF Token
     * @param token 访问Token
     * @return CSRF Token
     */
    String getCsrfToken(String token);
    
    /**
     * 验证CSRF Token
     * @param token 访问Token
     * @param csrfToken CSRF Token
     * @return 是否有效
     */
    boolean validateCsrf(String token, String csrfToken);
    
    /**
     * 使Token失效
     * @param token 访问Token
     * @return 是否成功
     */
    boolean invalidate(String token);
    
    /**
     * 生成刷新Token
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 刷新Token
     */
    String generateRefreshToken(String userNum, String orgCode);
}
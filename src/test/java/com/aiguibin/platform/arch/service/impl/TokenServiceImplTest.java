package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Token服务实现类单元测试
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TokenServiceImplTest {

    @Autowired
    private TokenService tokenService;

    private String userNum;
    private String orgCode;

    @BeforeEach
    void setUp() {
        userNum = "test_user_001";
        orgCode = "ORG_001";
    }

    /**
     * 测试生成Token
     */
    @Test
    void testIssueToken() {
        // 生成Token
        String token = tokenService.issueToken(userNum);
        assertNotNull(token, "生成的Token不应为null");
        assertFalse(token.isEmpty(), "生成的Token不应为空字符串");
        
        // 验证Token能正确获取用户编号
        String retrievedUserNum = tokenService.getUserNumByToken(token);
        assertEquals(userNum, retrievedUserNum, "通过Token获取的用户编号应与生成时的用户编号一致");
    }

    /**
     * 测试生成带机构信息的Token
     */
    @Test
    void testIssueTokenWithOrgCode() {
        // 生成带机构信息的Token
        String token = tokenService.issueToken(userNum, orgCode);
        assertNotNull(token, "生成的Token不应为null");
        assertFalse(token.isEmpty(), "生成的Token不应为空字符串");
        
        // 验证Token能正确获取用户编号
        String retrievedUserNum = tokenService.getUserNumFromToken(token);
        assertEquals(userNum, retrievedUserNum, "通过Token获取的用户编号应与生成时的用户编号一致");
    }

    /**
     * 测试CSRF Token功能
     */
    @Test
    void testCsrfToken() {
        // 生成Token
        String token = tokenService.issueToken(userNum);
        
        // 获取CSRF Token
        String csrfToken = tokenService.getCsrfToken(token);
        assertNotNull(csrfToken, "生成的CSRF Token不应为null");
        assertFalse(csrfToken.isEmpty(), "生成的CSRF Token不应为空字符串");
        
        // 验证CSRF Token
        boolean isValid = tokenService.validateCsrf(token, csrfToken);
        assertTrue(isValid, "CSRF Token验证应通过");
        
        // 验证无效的CSRF Token
        boolean isInvalid = tokenService.validateCsrf(token, "invalid_csrf_token");
        assertFalse(isInvalid, "无效的CSRF Token验证应失败");
    }

    /**
     * 测试Token失效功能
     */
    @Test
    void testInvalidateToken() {
        // 生成Token
        String token = tokenService.issueToken(userNum);
        
        // 验证Token有效
        assertNotNull(tokenService.getUserNumByToken(token), "Token应有效");
        
        // 使Token失效
        boolean isInvalidated = tokenService.invalidate(token);
        assertTrue(isInvalidated, "Token失效操作应成功");
        
        // 验证Token已失效
        assertNull(tokenService.getUserNumByToken(token), "失效后的Token应无法获取用户编号");
    }

    /**
     * 测试生成刷新Token
     */
    @Test
    void testGenerateRefreshToken() {
        // 生成刷新Token
        String refreshToken = tokenService.generateRefreshToken(userNum, orgCode);
        assertNotNull(refreshToken, "生成的刷新Token不应为null");
        assertFalse(refreshToken.isEmpty(), "生成的刷新Token不应为空字符串");
    }
}

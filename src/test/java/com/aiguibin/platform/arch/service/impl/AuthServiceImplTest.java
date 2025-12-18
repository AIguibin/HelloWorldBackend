package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthService实现类测试
 */
@SpringBootTest
class AuthServiceImplTest {

    @Autowired
    private AuthService authService;

    @Test
    void testCheckOrgAccess() {
        // 测试数据：使用系统默认的超级管理员账号和机构
        String userNum = "administrator";
        String orgCode = "99999";
        
        try {
            Map<String, Object> result = authService.checkOrgAccess(userNum, orgCode);
            
            // 验证基本返回结构
            assertNotNull(result, "返回结果不应为空");
            assertNotNull(result.get("user"), "应返回用户信息");
            assertNotNull(result.get("currentOrg"), "应返回当前机构信息");
            assertNotNull(result.get("permissions"), "应返回权限信息");
            assertNotNull(result.get("dataScope"), "应返回数据范围信息");
            
            // 验证用户信息
            Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
            assertEquals(userNum, userInfo.get("userNum"), "用户编号应匹配");
            
            // 验证当前机构信息
            Map<String, Object> currentOrg = (Map<String, Object>) result.get("currentOrg");
            assertEquals(orgCode, currentOrg.get("orgCode"), "机构编码应匹配");
            
            // 验证权限信息
            Map<String, Object> permissions = (Map<String, Object>) result.get("permissions");
            assertNotNull(permissions.get("menus"), "应返回菜单列表");
            assertNotNull(permissions.get("perms"), "应返回权限列表");
            
            // 验证数据范围信息
            Map<String, Object> dataScope = (Map<String, Object>) result.get("dataScope");
            assertNotNull(dataScope.get("scopeType"), "应返回数据范围类型");
            assertNotNull(dataScope.get("scopeTypeLabel"), "应返回数据范围标签");
            
            System.out.println("checkOrgAccess测试通过！");
            System.out.println("用户数据范围：" + dataScope.get("scopeTypeLabel"));
            System.out.println("返回结果包含字段：" + result.keySet());
            
        } catch (Exception e) {
            fail("测试失败，异常信息：" + e.getMessage());
        }
    }
    
    @Test
    void testCheckOrgAccessWithInvalidUser() {
        // 测试用户不存在的情况
        String invalidUserNum = "invalid_user";
        String orgCode = "99999";
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.checkOrgAccess(invalidUserNum, orgCode);
        });
        
        assertTrue(exception.getMessage().contains("用户不存在"), "应提示用户不存在");
        System.out.println("无效用户测试通过！");
    }
    
    @Test
    void testCheckOrgAccessWithInvalidOrg() {
        // 测试机构不存在的情况
        String userNum = "administrator";
        String invalidOrgCode = "invalid_org";
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.checkOrgAccess(userNum, invalidOrgCode);
        });
        
        assertTrue(exception.getMessage().contains("机构不存在"), "应提示机构不存在");
        System.out.println("无效机构测试通过！");
    }
    
    @Test
    void testCalculateDataScope() {
        // 测试数据范围计算逻辑
        // 这里可以直接测试calculateDataScope方法，但由于它是private的，我们通过checkOrgAccess间接测试
        String userNum = "administrator";
        String orgCode = "99999";
        
        Map<String, Object> result = authService.checkOrgAccess(userNum, orgCode);
        Map<String, Object> dataScope = (Map<String, Object>) result.get("dataScope");
        
        // 超级管理员应具有全部数据范围
        Integer scopeType = (Integer) dataScope.get("scopeType");
        assertTrue(scopeType <= 2, "超级管理员的数据范围应小于等于2（本机构或全部数据）");
        
        System.out.println("数据范围计算测试通过！超级管理员数据范围：" + dataScope.get("scopeTypeLabel"));
    }
}

package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.Menu;
import com.aiguibin.platform.arch.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 菜单控制器测试类，验证菜单API功能正确性
 */
@SpringBootTest
@AutoConfigureMockMvc
class MenuControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private MenuService menuService;
    
    @BeforeEach
    void setUp() {
        // 测试前确保菜单数据已初始化
        System.out.println("开始测试菜单API...");
    }
    
    @Test
    void getUserMenus() throws Exception {
        // 模拟管理员用户ID（通常为1）
        Long adminUserId = 1L;
        
        // 测试获取菜单树API
        mockMvc.perform(MockMvcRequestBuilders.get("/api/menus/user")
                .header("Authorization", "Bearer test-token")
                .header("X-User-Id", adminUserId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andDo(result -> {
                    System.out.println("API响应: " + result.getResponse().getContentAsString());
                });
        
        // 直接测试服务层方法
        List<Menu> menuTree = menuService.getUserMenuTree(adminUserId);
        assertNotNull(menuTree, "菜单树不应为空");
        assertTrue(menuTree.size() >= 5, "应至少包含5个主菜单");
        
        // 验证首页菜单存在
        boolean hasHomeMenu = menuTree.stream()
                .anyMatch(menu -> "首页".equals(menu.getMenuName()));
        assertTrue(hasHomeMenu, "菜单中应包含首页");
        
        // 验证变更管理菜单及其子菜单
        Menu changeMenu = menuTree.stream()
                .filter(menu -> "变更管理".equals(menu.getMenuName()))
                .findFirst()
                .orElse(null);
        assertNotNull(changeMenu, "菜单中应包含变更管理");
        assertNotNull(changeMenu.getChildren(), "变更管理应包含子菜单");
        assertTrue(changeMenu.getChildren().size() >= 2, "变更管理应至少包含2个子菜单");
        
        System.out.println("菜单API测试通过！成功获取到" + menuTree.size() + "个主菜单");
    }
    
    @Test
    void getUserPermissions() throws Exception {
        // 模拟管理员用户ID
        Long adminUserId = 1L;
        
        // 测试获取权限列表API
        mockMvc.perform(MockMvcRequestBuilders.get("/api/menus/permissions")
                .header("Authorization", "Bearer test-token")
                .header("X-User-Id", adminUserId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
        
        // 直接测试服务层方法
        List<String> permissions = menuService.getUserPermissions(adminUserId);
        assertNotNull(permissions, "权限列表不应为空");
        assertFalse(permissions.isEmpty(), "权限列表应包含至少一个权限");
        
        System.out.println("权限API测试通过！成功获取到" + permissions.size() + "个权限");
    }
}
package com.aiguibin.platform.arch.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MenuMapper的单元测试类
 */
@SpringBootTest
@Transactional
public class MenuMapperTest {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 测试selectByMenuType方法
     */
    @Test
    public void testSelectByMenuType() {
        // 测试查询页面类型菜单
        List<Map<String, Object>> pageMenus = menuMapper.selectByMenuType("P");
        
        // 验证结果
        assertNotNull(pageMenus);
        // 可以根据实际数据情况调整断言
        // assertTrue(pageMenus.size() > 0, "页面菜单列表不能为空");
        
        // 验证返回的菜单信息包含必要字段
        if (!pageMenus.isEmpty()) {
            Map<String, Object> pageMenu = pageMenus.get(0);
            assertNotNull(pageMenu.get("menuCode"), "菜单编码不能为空");
            assertNotNull(pageMenu.get("menuName"), "菜单名称不能为空");
            assertNotNull(pageMenu.get("menuType"), "菜单类型不能为空");
            assertEquals("P", pageMenu.get("menuType"), "菜单类型应该为页面类型");
        }
    }

    /**
     * 测试selectButtonsByParentCode方法
     */
    @Test
    public void testSelectButtonsByParentCode() {
        // 准备测试数据
        // 这里使用一个示例的父菜单编码，实际测试时需要替换为真实的父菜单编码
        String parentMenuCode = "system";
        
        // 执行查询
        List<Map<String, Object>> buttons = menuMapper.selectButtonsByParentCode(parentMenuCode);
        
        // 验证结果
        assertNotNull(buttons);
        // 可以根据实际数据情况调整断言
        // 即使没有按钮权限，返回的列表也应该不为null
        
        // 验证返回的按钮信息包含必要字段
        if (!buttons.isEmpty()) {
            Map<String, Object> button = buttons.get(0);
            assertNotNull(button.get("menuCode"), "按钮编码不能为空");
            assertNotNull(button.get("menuName"), "按钮名称不能为空");
            assertNotNull(button.get("menuType"), "菜单类型不能为空");
            assertEquals("B", button.get("menuType"), "菜单类型应该为按钮类型");
        }
    }

    /**
     * 测试selectMenusByUserId方法
     */
    @Test
    public void testSelectMenusByUserId() {
        // 准备测试数据
        Long userId = 1L;
        
        // 执行查询
        List<com.aiguibin.platform.arch.entity.Menu> menus = menuMapper.selectMenusByUserId(userId);
        
        // 验证结果
        assertNotNull(menus);
        // 可以根据实际数据情况调整断言
        // 即使没有菜单，返回的列表也应该不为null
    }

    /**
     * 测试selectMenusByRoleId方法
     */
    @Test
    public void testSelectMenusByRoleId() {
        // 准备测试数据
        Long roleId = 1L;
        
        // 执行查询
        List<com.aiguibin.platform.arch.entity.Menu> menus = menuMapper.selectMenusByRoleId(roleId);
        
        // 验证结果
        assertNotNull(menus);
        // 可以根据实际数据情况调整断言
        // 即使没有菜单，返回的列表也应该不为null
    }
}

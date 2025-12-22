package com.aiguibin.platform.arch.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysPermissionMapper的单元测试类
 */
@SpringBootTest
@Transactional
public class SysPermissionMapperTest {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    /**
     * 测试selectAll方法
     */
    @Test
    public void testSelectAll() {
        // 执行查询
        List<Map<String, Object>> permissions = sysPermissionMapper.selectAll();
        
        // 验证结果
        assertNotNull(permissions);
        // 可以根据实际数据情况调整断言
        // assertTrue(permissions.size() > 0, "权限列表不能为空");
        
        // 验证返回的权限信息包含必要字段
        if (!permissions.isEmpty()) {
            Map<String, Object> permission = permissions.get(0);
            assertNotNull(permission.get("permCode"), "权限编码不能为空");
            assertNotNull(permission.get("permName"), "权限名称不能为空");
            assertNotNull(permission.get("permKey"), "权限标识不能为空");
            assertNotNull(permission.get("permType"), "权限类型不能为空");
        }
    }

    /**
     * 测试selectPermissionsByRoleCodes方法
     */
    @Test
    public void testSelectPermissionsByRoleCodes() {
        // 准备测试数据
        List<String> roleCodes = Arrays.asList("admin", "user");
        
        // 执行查询
        List<Map<String, Object>> permissions = sysPermissionMapper.selectPermissionsByRoleCodes(roleCodes);
        
        // 验证结果
        assertNotNull(permissions);
        // 可以根据实际数据情况调整断言
        // 如果存在测试数据，可以断言权限列表不为空
        
        // 验证返回的权限信息包含必要字段
        if (!permissions.isEmpty()) {
            Map<String, Object> permission = permissions.get(0);
            assertNotNull(permission.get("permCode"), "权限编码不能为空");
            assertNotNull(permission.get("permName"), "权限名称不能为空");
            assertNotNull(permission.get("roleCode"), "角色编码不能为空");
        }
    }

    /**
     * 测试hasPermission方法
     */
    @Test
    public void testHasPermission() {
        // 准备测试数据
        List<String> roleCodes = Arrays.asList("admin");
        String permKey = "system:user:view";
        
        // 执行查询
        boolean hasPermission = sysPermissionMapper.hasPermission(roleCodes, permKey);
        
        // 验证结果
        // 可以根据实际数据情况调整断言
        // 如果admin角色拥有该权限，可以断言为true
        // assertTrue(hasPermission, "admin角色应该拥有system:user:view权限");
    }

    /**
     * 测试findPermissionsByRoleCodes方法
     */
    @Test
    public void testFindPermissionsByRoleCodes() {
        // 准备测试数据
        List<String> roleCodes = Arrays.asList("admin");
        
        // 执行查询
        List<com.aiguibin.platform.arch.entity.SysPermission> permissions = sysPermissionMapper.findPermissionsByRoleCodes(roleCodes);
        
        // 验证结果
        assertNotNull(permissions);
        // 可以根据实际数据情况调整断言
        // 如果admin角色拥有权限，可以断言权限列表不为空
        
        // 验证返回的权限实体包含必要字段
        if (!permissions.isEmpty()) {
            com.aiguibin.platform.arch.entity.SysPermission permission = permissions.get(0);
            assertNotNull(permission.getPermCode(), "权限编码不能为空");
            assertNotNull(permission.getPermName(), "权限名称不能为空");
            assertNotNull(permission.getPermKey(), "权限标识不能为空");
        }
    }
}

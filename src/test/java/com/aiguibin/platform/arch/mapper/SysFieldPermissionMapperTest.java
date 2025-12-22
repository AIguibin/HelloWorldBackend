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
 * SysFieldPermissionMapper的单元测试类
 */
@SpringBootTest
@Transactional
public class SysFieldPermissionMapperTest {

    @Autowired
    private SysFieldPermissionMapper sysFieldPermissionMapper;

    /**
     * 测试selectByPermCodes方法
     */
    @Test
    public void testSelectByPermCodes() {
        // 准备测试数据
        List<String> permCodes = Arrays.asList("user:read", "user:write");
        
        // 执行查询
        List<Map<String, Object>> fieldPermissions = sysFieldPermissionMapper.selectByPermCodes(permCodes);
        
        // 验证结果
        assertNotNull(fieldPermissions);
        // 可以根据实际数据情况调整断言
        // 即使没有字段权限，返回的列表也应该不为null
        
        // 验证返回的字段权限信息包含必要字段
        if (!fieldPermissions.isEmpty()) {
            Map<String, Object> fieldPermission = fieldPermissions.get(0);
            assertNotNull(fieldPermission.get("permCode"), "权限编码不能为空");
            assertNotNull(fieldPermission.get("fieldCode"), "字段编码不能为空");
            assertNotNull(fieldPermission.get("entityType"), "实体类型不能为空");
            assertNotNull(fieldPermission.get("fieldName"), "字段名称不能为空");
            assertNotNull(fieldPermission.get("fieldType"), "字段类型不能为空");
        }
    }
}

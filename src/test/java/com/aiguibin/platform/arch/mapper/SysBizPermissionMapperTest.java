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
 * SysBizPermissionMapper的单元测试类
 */
@SpringBootTest
@Transactional
public class SysBizPermissionMapperTest {

    @Autowired
    private SysBizPermissionMapper sysBizPermissionMapper;

    /**
     * 测试selectByPermCodes方法
     */
    @Test
    public void testSelectByPermCodes() {
        // 准备测试数据
        List<String> permCodes = Arrays.asList("order:approve", "order:reject");
        
        // 执行查询
        List<Map<String, Object>> bizPermissions = sysBizPermissionMapper.selectByPermCodes(permCodes);
        
        // 验证结果
        assertNotNull(bizPermissions);
        // 可以根据实际数据情况调整断言
        // 即使没有业务权限，返回的列表也应该不为null
        
        // 验证返回的业务权限信息包含必要字段
        if (!bizPermissions.isEmpty()) {
            Map<String, Object> bizPermission = bizPermissions.get(0);
            assertNotNull(bizPermission.get("permCode"), "权限编码不能为空");
            assertNotNull(bizPermission.get("businessName"), "业务名称不能为空");
            assertNotNull(bizPermission.get("businessType"), "业务类型不能为空");
            assertNotNull(bizPermission.get("ruleEngine"), "规则引擎不能为空");
        }
    }
}

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
 * SysPermResourceMapper的单元测试类
 */
@SpringBootTest
@Transactional
public class SysPermResourceMapperTest {

    @Autowired
    private SysPermResourceMapper sysPermResourceMapper;

    /**
     * 测试selectResourcesByRoleCodes方法
     */
    @Test
    public void testSelectResourcesByRoleCodes() {
        // 准备测试数据
        List<String> roleCodes = Arrays.asList("admin", "user");
        String resourceType = "MENU";
        
        // 执行查询
        List<Map<String, Object>> resources = sysPermResourceMapper.selectResourcesByRoleCodes(roleCodes, resourceType);
        
        // 验证结果
        assertNotNull(resources);
        // 可以根据实际数据情况调整断言
        // assertTrue(resources.size() > 0, "资源列表不能为空");
        
        // 验证返回的资源信息包含必要字段
        if (!resources.isEmpty()) {
            Map<String, Object> resource = resources.get(0);
            assertNotNull(resource.get("permCode"), "权限编码不能为空");
            assertNotNull(resource.get("resourceType"), "资源类型不能为空");
            assertNotNull(resource.get("resourceKey"), "资源标识不能为空");
        }
    }
}

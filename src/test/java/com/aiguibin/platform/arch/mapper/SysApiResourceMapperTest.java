package com.aiguibin.platform.arch.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysApiResourceMapper的单元测试类
 */
@SpringBootTest
@Transactional
public class SysApiResourceMapperTest {

    @Autowired
    private SysApiResourceMapper sysApiResourceMapper;

    /**
     * 测试selectAll方法
     */
    @Test
    public void testSelectAll() {
        // 执行查询
        List<Map<String, Object>> apiResources = sysApiResourceMapper.selectAll();
        
        // 验证结果
        assertNotNull(apiResources);
        // 可以根据实际数据情况调整断言
        // assertTrue(apiResources.size() > 0, "API资源列表不能为空");
        
        // 验证返回的API资源信息包含必要字段
        if (!apiResources.isEmpty()) {
            Map<String, Object> apiResource = apiResources.get(0);
            assertNotNull(apiResource.get("apiCode"), "API编码不能为空");
            assertNotNull(apiResource.get("apiName"), "API名称不能为空");
            assertNotNull(apiResource.get("apiPath"), "API路径不能为空");
            assertNotNull(apiResource.get("httpMethod"), "HTTP方法不能为空");
            assertNotNull(apiResource.get("resourceKey"), "资源标识不能为空");
        }
    }
}

package com.aiguibin.platform.arch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 启动冒烟测试：验证 Spring 上下文可完整装配（骨架不依赖数据库即可通过）.
 */
@SpringBootTest
class SpringbootStarterApplicationTests {

    @Test
    void contextLoads() {
        // 上下文装配成功即通过
    }
}

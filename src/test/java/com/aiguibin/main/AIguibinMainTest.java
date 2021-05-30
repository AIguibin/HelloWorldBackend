package com.aiguibin.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * 描述： 测试
 *
 * @author AIguibin Date time 2018/12/29 1:59
 */
//@Log4j2
public class AIguibinMainTest {
    private static Logger logger=LogManager.getLogger(AIguibinMainTest.class);
    @Test
    public void Testlombok5log4j2(){
        logger.info(String.format("我是@Log4j2注解的lombok的日志：%s---执行顺序=%d","请注意%s与%d的区别",1));
        System.out.println("=======================日志我是@Log4j2测试异步的第三条===========================");
    }
}

package com.aiguibin.customize.ServletConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 指明映射关系
 *
 * @author AIguibin
 * Date time 2019年04月26日 23:41:01
 */
public class ServletConfig {
    public static List<MappingBean> servletMapping=new ArrayList<>();
    static {
        servletMapping.add(new MappingBean("helloController","/helloController","com.aiguibin.customize.TestController.HelloController"));
    }
}

package com.aiguibin.server;

import com.aiguibin.config.RestConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 描述： 
 *
 * @author AIguibin Date time 2018/8/9 9:19
 */
public class StartServer {
    private static final Log logger = LogFactory.getLog(StartServer.class);
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(RestConfig.class);
        System.out.println("Press any key to shutdown......");
        System.in.read();
        context.close();
    }
}

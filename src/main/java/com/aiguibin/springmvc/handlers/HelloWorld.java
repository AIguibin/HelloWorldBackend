package com.aiguibin.springmvc.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author AIguibin
 */
@Controller
public class HelloWorld {
    // private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    private static Log logger=LogFactory.getLog(HelloWorld.class);

    /**
     * @return
     */
    @RequestMapping("/helloworld")
    public String hello(String str) {
        System.out.println("hello");
        str.substring(1, 5);
        logger.error("log4j2日志");
        return "success";
    }
}

package com.aiguibin.springbootnative.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 欢迎界面
 *
 * @author AIguibin
 * Date time 2019年04月05日 22:34:48
 */
@RestController
@RequestMapping(value = "/index")
public class HelloWorldController {
    private static final Log logger=LogFactory.getLog(HelloWorldController.class);

    //获取配置文件
    @Value(value = "${randomNum}")
    private  String randomNum;


    /**
     * 开箱即用的特点，springboot集成了MVC
     * @author AIguibin
     * Date time 2019/4/7 9:39
     * @return
     */
    @RequestMapping(value = "/world")
    public String helloWorld(){
        logger.debug(randomNum);
        return "world";
    }
}

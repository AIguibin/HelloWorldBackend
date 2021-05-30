package com.aiguibin.springbootnative.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Thymeleaf模板控制层
 *
 * @author AIguibin
 * Date time 2019年04月06日 20:14:05
 */
@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafController {
    //日志声明
    private static final Log logger = LogFactory.getLog(ThymeleafController.class);

    @RequestMapping(value = "/index")
    public String thymeleaf(){
        return "thymeleaf";
    }
}

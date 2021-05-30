package com.aiguibin.springbootnative.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 注解版Servlet
 * 前提要在启动类上添加@ServletComponentScan注解
 * 扫描我们添加的@WebServlet,@WebFilter,@WebListener注解的类
 * 必不可少的参数urlPatterns表示访问路径
 *
 * @author AIguibin
 * Date time 2019年04月06日 16:39:20
 */


@WebServlet(urlPatterns = "/hello",name ="helloWorldServlet" )
public class HelloWorldServlet extends HttpServlet {
    //日志声明
    private static final Log logger = LogFactory.getLog(HelloWorldServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        logger.debug("请求来了");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        this.doGet(request,response);
    }
}

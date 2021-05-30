package com.aiguibin.customize;

import com.aiguibin.customize.ServerTomcat.Tomcat;

import java.io.IOException;

/**
 * 主程序启动服务类
 *
 * @author AIguibin
 * Date time 2019年04月25日 14:50:06
 */
public class StartWebServer {

    public static void main(String[] args) throws IOException {
        Tomcat tomcat=new Tomcat(8080);
        tomcat.start();
        System.in.read();
    }
}

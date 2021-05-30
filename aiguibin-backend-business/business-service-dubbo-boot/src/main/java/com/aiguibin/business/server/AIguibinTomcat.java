package com.aiguibin.business.server;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * 配置内嵌Tomcat
 *
 * @author AIguibin
 * Date time 2019年05月05日 16:28:01
 */
public class AIguibinTomcat {
    public static void start() throws LifecycleException {
        //创建Tomcat
        Tomcat tomcat=new Tomcat();
        //设置端口号
        tomcat.setPort(8000);
        //设置主机名称
        tomcat.setHostname("127.0.0.1");
        //设置Tomcat临时工作目录
        tomcat.setBaseDir("G:/TomcatBaseDir");
        /**
         * 创建上下文 读取项目路径 加载静态资源
         * A context path must either be an empty string or start with a '/' and do not end with a '/'
         * 上下文路径必须是一个空字符串，或者以“/”开头，不要以“/”结尾。
         * contextPath 不能设置为"/" 否则报以上错误。解决办法如下设置为空 "";
         *
         * 主要的几个概念
         * BaseDir：Tomcat工作的临时目录
         * appBase：是指定WebApps的目录,该目录下的子目录将自动被部署为应用,例如安装版Tomcat中webapps目录
         * docBase：指向了你某个应用的目录,即该项目的绝对路径，可以和appBase没有任何关系
         * 部署web应用
         * tomcat.addWebapp(contextPath,docBase)
         */
        StandardContext standardContext= (StandardContext) tomcat.addWebapp("",new File("").getAbsolutePath());
        //禁止重载入
        standardContext.setReloadable(false);
        // 读取Classes
        File addWebInfoClasses=new File("target/classes");
        // 创建WebResources
        WebResourceRoot resources=new StandardRoot(standardContext);
        // Tomcat读取class文件执行（创建虚拟的文件目录） Tomcat内部读取执行
        resources.addPreResources(new DirResourceSet(resources,"/WEB-INF/classes",addWebInfoClasses.getAbsolutePath(),"/"));
        //启动Tomcat
        tomcat.start();
        //异步等待请求
        tomcat.getServer().await();
    }
}

package com.aiguibin.webservice;

import com.aiguibin.webservice.server.HelloWebServiceImpl;

import javax.xml.ws.Endpoint;

/**
 * 发布WebService服务
 *
 * @author AIguibin
 * Date time 2019年04月23日 17:05:41
 */
public class StartServer {
    /**
     * 主函数
     * @author AIguibin
     * Date time 2019/4/23 17:06
     * @return void
     */
    public static void main(String[] args) {
        Endpoint.publish("http://127.0.0.1:8080/Service/HelloWebService", new HelloWebServiceImpl());
        System.out.println("发布成功!");
        //发布成功后 在浏览器输入 http://127.0.0.1:8080/Service/HelloWebService?wsdl
    }
}

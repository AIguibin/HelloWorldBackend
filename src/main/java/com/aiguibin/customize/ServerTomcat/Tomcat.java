package com.aiguibin.customize.ServerTomcat;

import com.aiguibin.customize.ServerServlet.HttpServlet;
import com.aiguibin.customize.ServerServlet.Request;
import com.aiguibin.customize.ServerServlet.Response;
import com.aiguibin.customize.ServletConfig.MappingBean;
import com.aiguibin.customize.ServletConfig.ServletConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Tomcat实现
 *
 * @author AIguibin
 * Date time 2019年04月25日 17:48:16
 */
public class Tomcat {
    /**
     * 端口号
     */
    private int port;
    private Map<String, String> servletUrl = new HashMap<>();

    /**
     *
     * @author AIguibin
     * Date time 2019/4/26 23:49
     */
    public Tomcat(int port) {
        this.port = port;
    }
    /**
     *
     * @author AIguibin
     * Date time 2019/4/26 23:49
     */
    public void start(){
        init();
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(port);
            System.out.println("Tomcat is started on 8080");

            while (true) {
                Socket socket=serverSocket.accept();
                InputStream inputStream=socket.getInputStream();
                OutputStream outputStream=socket.getOutputStream();
                Request request=new Request(inputStream);
                Response response= new Response(outputStream);
                dispatch(request,response);
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *
     * @author AIguibin
     * Date time 2019/4/26 23:49
     */
    public void init(){
        for (MappingBean mappingBean : ServletConfig.servletMapping) {
            servletUrl.put(mappingBean.getUrl(),mappingBean.getClazz());
        }
    }
    /**
     *
     * @author AIguibin
     * Date time 2019/4/26 23:48
     * @param request 请求
     * @param response 响应
     * @return
     */
    public void dispatch(Request request, Response response) throws Exception {
        String clazz=servletUrl.get(request.getUrl());
        Class<HttpServlet> httpServletClass= (Class<HttpServlet>) Class.forName(clazz);
        HttpServlet httpServlet=httpServletClass.newInstance();
        httpServlet.service(request, response);
    }
}

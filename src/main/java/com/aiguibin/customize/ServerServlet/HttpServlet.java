package com.aiguibin.customize.ServerServlet;

/**
 * Servlet 请求处理基类
 * Tomcat是满足Servlet规范的容器，那么自然Tomcat需要提供API。
 * 这里你看到了Servlet常见的doGet/doPost/service方法
 *
 * @author AIguibin
 * Date time 2019年04月25日 17:28:04
 */
public abstract class HttpServlet {

    public abstract void doGet(Request request, Response response);

    public abstract void doPost(Request request, Response response);

    public void service(Request request, Response response) {
        if (request.getMethod().equalsIgnoreCase("POST")) {
            doPost(request, response);
        } else if (request.getMethod().equalsIgnoreCase("GET")) {
            doGet(request, response);
        }
    }
}

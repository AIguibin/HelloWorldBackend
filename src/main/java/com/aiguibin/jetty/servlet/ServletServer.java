package com.aiguibin.jetty.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ServletServer extends HttpServlet {
    private String path = "/";

    /**
     * 默认的构造函数
     */
    public ServletServer() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param path 路径
     */
    public ServletServer(String path) {
        this();
        this.path = path;
    }

    /**
     * 获取Http Servlet对应的路径
     *
     * @return 路径
     */
    public String getPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     * @see HttpServlet#service(HttpServletRequest, HttpServletResponse)
     */

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }
}

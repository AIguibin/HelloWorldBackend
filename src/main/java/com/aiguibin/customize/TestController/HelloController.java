package com.aiguibin.customize.TestController;

import com.aiguibin.customize.ServerServlet.HttpServlet;
import com.aiguibin.customize.ServerServlet.Request;
import com.aiguibin.customize.ServerServlet.Response;

import java.io.IOException;

/**
 * 
 *
 * @author AIguibin
 * Date time 2019年04月25日 17:42:36
 */
public class HelloController extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        try {
            response.write(request.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        doGet(request, response);
    }
}

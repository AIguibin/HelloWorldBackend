package com.aiguibin.action;

import com.aiguibin.common.character.StringHelper;
import com.aiguibin.jetty.servlet.ServletServer;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
@Scope(value = "prototype")
public class FirstServlet extends ServletServer {
    private static final Log logger = LogFactory.getLog(FirstServlet.class);

    public FirstServlet() {
    }

    public FirstServlet(String path) {
        super("/first");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s",
                    request.getMethod(),
                    request.getCookies(),
                    request.getAuthType(),
                    request.getPathInfo(),
                    request.getContextPath()));
        }
        String type=request.getParameter("type");
        if (StringHelper.isEmpty(type)){
            if (logger.isErrorEnabled()) {
                logger.error(String.format("参数为空:%s.",type));
            }
        }
        if (type.equals("JSON")) {
            response.setCharacterEncoding("UTF-8");
            JSONObject result=new JSONObject();
            result.put("data","AnyObjectData");
            response.getWriter().write(result.toJSONString());
            response.getWriter().close();
        }
        if (type.equals("Binary")){
            response.addHeader("Content-Disposition", "attachment;filename=" + "fileName");
            //获取文件二进制输入流
            FileInputStream fis = new FileInputStream(new File(""));
            byte[] b = new byte[1024];
            int length;
            OutputStream out = response.getOutputStream();
            while ((length = fis.read(b)) > 0) {
                out.write(b, 0, length);
            }
            out.flush();
            out.close();
            fis.close();
        }
    }
}

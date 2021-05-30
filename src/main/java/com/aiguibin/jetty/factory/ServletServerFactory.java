package com.aiguibin.jetty.factory;

import com.aiguibin.jetty.bean.ServletServerConfigBean;
import com.aiguibin.jetty.helper.StringHelper;
import com.aiguibin.jetty.servlet.ServletServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class ServletServerFactory extends HttpServerFactory {
    private static final Log logger = LogFactory.getLog(ServletServerFactory.class);

    private ServletServerConfigBean servletServerConfigBean;
    private ApplicationContext context;

    public ServletServerFactory(ApplicationContext context, ServletServerConfigBean servletServerConfigBean) {
        super(servletServerConfigBean);
        this.context = context;
        this.servletServerConfigBean = servletServerConfigBean;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerFactory#getHandler()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Handler getHandler() {
        String[] classesDefs = servletServerConfigBean.getServiceClasses();
        if (classesDefs == null || classesDefs.length <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("You not define any servlet constant.");
            }
            return null;
        } else {
            ServletContextHandler contextHandler = new ServletContextHandler(1);
            contextHandler.setContextPath("/");
            for (String classesDef : classesDefs) {
                if (!StringHelper.isBlank(classesDef)) {
                    List<Class<?>> servletClasses = (List) context.getBean(classesDef, List.class);
                    if (!servletClasses.isEmpty()) {
                        servletClasses.forEach((clazz) -> {
                            ServletServer servlet = (ServletServer) context.getBean(clazz);
                            contextHandler.addServlet(new ServletHolder(servlet), servlet.getPath());
                        });
                    }
                }
            }
            return contextHandler;
        }
    }
}

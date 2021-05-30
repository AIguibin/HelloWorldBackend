package com.aiguibin.service.impl;

import com.aiguibin.service.face.FtpService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class FtpServiceImpl implements FtpService,InitializingBean,DisposableBean {
    @Override
    public void afterPropertiesSet() throws Exception {
    }
    @Override
    public void destroy() throws Exception {
    }


}

package com.aiguibin.business.client;

import com.aiguibin.business.api.HelloBusinessApi;
import com.aiguibin.business.constants.DubboConstants;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class HelloBusinessClient {
    @Reference(interfaceClass = HelloBusinessApi.class, version =DubboConstants.DUBBO_API_VERSION)
    private HelloBusinessApi helloBusinessApi;
}

package com.aiguibin.userinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 描述： 用户信息提供者
 *
 * @author AIguibin Date time 2019/1/6 19:12
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AiguibinUserInfoProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiguibinUserInfoProviderApplication.class, args);
	}

}


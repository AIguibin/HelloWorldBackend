package com.aiguibin.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 访问地址格式
 * 服务IP+服务端口号+项目名称+版本名称+分支名称
 * 服务地址 = 127.0.0.1:8888
 * 项目名称 = 仓库文件夹名 ，其实这个就是一个文件夹名称，只是为了更好的扩展，规范写法
 * 版本名称 = dev，pro，test 这个项目并没有指定什么版本，所有只要是这三个中任意一个就可以
 * 分支名称 = master 或其他你创建的分支
 * 例如 : 127.0.0.1:8888/HelloWorldSpringCloud/dev/master
 * ======
 * 客户端使用方式
 * 添加优先级高于application的配置文件bootstrap.yml或bootstrap.properties
 * 配置其读取配置的访问地址
 *      spring.cloud.config.uri=http://127.0.0.1:8888
 *      spring.cloud.config.name=HelloWorldSpringCloud
 *      spring.cloud.config.profile=dev
 *      spring.cloud.config.label=slave_one
 * 其格式必须是这四级，否则与访问地址格式就不匹配，就无法获取
 * 为了区别多个模块的配置文件，强烈建议第二级项目名称写成模块名称，例如：aiguibin-userinfo-consumer
 * 项目启动会自动获取springcloud体系自身的配置属性，例如：eureka.client.fetchRegistry=true
 * 其本质是自动加载 application.yml或 application.properties 等配置文件
 * 所以在代码块中获取非体系的属性值 "test": "1234567" 使用 @Value("${test}") private String register;
 * =====
 * 客户端配置手动刷新
 * 添加 spring-boot-starter-actuator 依赖
 * 添加 @RefreshScope 注解
 * 通过配置 management.endpoints.web.exposure.include=refresh
 * 只有把refresh接入点显式暴露出来，才可以通过POST 调用/actuator/refresh 来更新配置
 * ==========
 * 加密解密
 * 首先解除JDK本身加密解密的限制
 * 然后在configServer 中创建bootstrap.yml 或 bootstrap.properties 配置文件
 * 访问 configServer 查看加密解密状态 127.0.0.1:8888/encrypt/status
 * 设置配置秘钥 encrypt.key=aiguibin
 * 通过POST提交text文本类型的待加密的字符串aiguibin 访问 127.0.0.1:8888/encrypt
 * 会得到加密后的字符串 981969692780484b90a305aad5904f58576a350b6716b3f93bc1a70fb42ae1ac
 * 通过POST提交text文本类型的已加密的字符串 访问 127.0.0.1:8888/decrypt会得到aiguibin
 * 注意在配置文件中使用加密后的字符的时候要加上{cipher}例如 ：token={cipher}b3f93bc1a70fb42ae1ac
 * 还要注意.yml格式的时候要加引号 例如 ：token:'{cipher}b3f93bc1a70fb42ae1ac'
 */

@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class AiguibinCloudConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiguibinCloudConfigApplication.class, args);
    }
}


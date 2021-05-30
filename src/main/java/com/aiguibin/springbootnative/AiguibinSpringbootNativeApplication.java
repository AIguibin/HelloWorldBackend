package com.aiguibin.springbootnative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class AiguibinSpringbootNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiguibinSpringbootNativeApplication.class, args);
    }

}

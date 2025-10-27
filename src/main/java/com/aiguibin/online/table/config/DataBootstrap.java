package com.aiguibin.online.table.config;

import com.aiguibin.online.table.entity.User;
import com.aiguibin.online.table.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DataBootstrap implements CommandLineRunner {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Long count = userMapper.selectCount(null);
        if (count == null || count == 0L) {
            User u = new User();
            u.setUsernumb("admin");
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("666666"));
            u.setRole("ADMIN");
            userMapper.insert(u);
        }
    }
}
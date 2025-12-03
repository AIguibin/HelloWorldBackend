package com.aiguibin.platform.arch.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserNum, username));
    }

    public boolean matchesPassword(String raw, String encoded) {
        System.out.println("Raw password: " + raw);
        System.out.println("Encoded password: " + encoded);
        boolean matches = passwordEncoder.matches(raw, encoded);
        System.out.println("Password match result: " + matches);
        return matches;
    }
}
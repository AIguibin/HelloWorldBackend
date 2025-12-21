package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token管理服务实现类
 * 负责Token的生成、验证、失效处理等
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> csrfStore = new ConcurrentHashMap<>();

    @Override
    public String issueToken(String userNum) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        tokenStore.put(token, userNum);
        String csrfToken = UUID.randomUUID().toString().replaceAll("-", "");
        csrfStore.put(token, csrfToken);
        return token;
    }

    @Override
    public String getUserNumByToken(String token) {
        return tokenStore.get(token);
    }

    @Override
    public String getCsrfToken(String token) {
        return csrfStore.get(token);
    }

    @Override
    public boolean validateCsrf(String token, String csrfToken) {
        String expect = csrfStore.get(token);
        return expect != null && expect.equals(csrfToken);
    }

    @Override
    public boolean invalidate(String token) {
        csrfStore.remove(token);
        return tokenStore.remove(token) != null;
    }

    @Override
    public String issueToken(String userNum, String orgCode) {
        // 生成包含机构信息的token
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        // 存储格式：userNum:orgCode
        tokenStore.put(token, userNum + ":" + orgCode);
        String csrfToken = UUID.randomUUID().toString().replaceAll("-", "");
        csrfStore.put(token, csrfToken);
        return token;
    }

    @Override
    public String getUserNumFromToken(String token) {
        String value = tokenStore.get(token);
        if (value != null) {
            // 如果存储的是userNum:orgCode格式，则只返回userNum
            if (value.contains(":")) {
                return value.split(":")[0];
            }
            return value;
        }
        return null;
    }

    @Override
    public String generateRefreshToken(String userNum, String orgCode) {
        // 生成刷新token
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
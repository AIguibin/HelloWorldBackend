package com.aiguibin.online.table.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> csrfStore = new ConcurrentHashMap<>();

    public String issueToken(String username) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        tokenStore.put(token, username);
        String csrfToken = UUID.randomUUID().toString().replaceAll("-", "");
        csrfStore.put(token, csrfToken);
        return token;
    }

    public String getUsernameByToken(String token) {
        return tokenStore.get(token);
    }

    public String getCsrfToken(String token) {
        return csrfStore.get(token);
    }

    public boolean validateCsrf(String token, String csrfToken) {
        String expect = csrfStore.get(token);
        return expect != null && expect.equals(csrfToken);
    }

    public boolean invalidate(String token) {
        csrfStore.remove(token);
        return tokenStore.remove(token) != null;
    }
}
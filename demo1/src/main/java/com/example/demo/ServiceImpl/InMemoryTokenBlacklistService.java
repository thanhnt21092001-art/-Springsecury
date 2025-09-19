package com.example.demo.ServiceImpl;

import com.example.demo.Service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class InMemoryTokenBlacklistService implements TokenBlacklistService {

    private final Map<String, LocalDateTime> blacklistedTokens = new ConcurrentHashMap<>();
    @Override
    public void blacklistToken(String token, LocalDateTime expiryDate) {
        blacklistedTokens.put(token, expiryDate);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        if (!blacklistedTokens.containsKey(token)) return false;
        return blacklistedTokens.get(token).isAfter(LocalDateTime.now());
    }
}

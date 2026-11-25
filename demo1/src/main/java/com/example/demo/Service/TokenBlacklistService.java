package com.example.demo.Service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public interface TokenBlacklistService {
    void blacklistToken(String token, LocalDateTime expiryDate);
    boolean isTokenBlacklisted(String token);
}

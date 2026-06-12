package com.example.demo.Service;

public interface OtpService {
    void verifyOtp(
            String email,
            String otp
    );
}

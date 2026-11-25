package com.example.demo.Service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException;
}

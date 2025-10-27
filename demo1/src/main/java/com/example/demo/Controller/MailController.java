package com.example.demo.Controller;

import com.example.demo.Service.EmailService;
import com.example.demo.dto.RequestSendMailDTO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send-html")
    public ResponseEntity<?> sendHtmlMail(@RequestBody RequestSendMailDTO dto) throws MessagingException {
        // HTML content có thể chứa style, link, logo, v.v.
        String html = """
            <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #4CAF50;">Xin chào, %s!</h2>
                    <p>Cảm ơn bạn đã đăng ký tài khoản tại <b>Demo App</b>.</p>
                    <p>Vui lòng nhấn vào liên kết sau để xác thực email:</p>
                    <a href="https://example.com/verify?email=%s" 
                       style="display:inline-block; padding:10px 15px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;">
                       Xác thực ngay
                    </a>
                    <p style="margin-top:20px; color:gray;">Nếu bạn không đăng ký, vui lòng bỏ qua email này.</p>
                </body>
            </html>
        """.formatted(dto.getName(), dto.getTo());
        emailService.sendHtmlMail(dto.getTo(), dto.getSubject(), html);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("message","HTML mail sent successfully!");
        map.put("code", String.valueOf(HttpServletResponse.SC_OK));
        return ResponseEntity.ok().body(map);
    }
}

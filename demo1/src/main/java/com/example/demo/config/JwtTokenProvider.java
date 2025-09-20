package com.example.demo.config;

import com.example.demo.Entities.User;
import com.example.demo.Enum.EnumConfig;
import com.example.demo.Repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Tạo key bảo mật
    private final UserRepository userRepository;
    private final long validityInMs = 3600000; // 1 giờ
    Logger logger = Logger.getLogger(JwtTokenProvider.class.getName());

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String username) {
        String userName = getUserNameData(username);
        User user = userRepository.findByUsername(userName).orElseThrow();
        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản đã bị thu hồi");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();


    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (RuntimeException e) {
            throw new RuntimeException("invalid token");
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Bỏ "Bearer " để lấy phần token
        }
        return null;
    }

    public LocalDateTime getExpirationDate(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key.getEncoded()) // Hoặc Base64 nếu bạn encode key
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration(); // Date kiểu java.util.Date

        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(); // Chuyển sang LocalDateTime
    }

    private String getUserNameData(String input) {

        Pattern pattern = Pattern.compile("Username=([^,\\]]+)");
        Matcher matcher = pattern.matcher(input);
        String username = "";
        if (matcher.find()) {
            username = matcher.group(1);
            logger.info("Username là: " + username);
        } else {
            logger.info("Không tìm thấy Username");
        }
        return username;
    }
}

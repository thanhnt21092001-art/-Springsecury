package com.example.demo.Controller;

import com.example.demo.Entities.User;
import com.example.demo.Enum.EnumConfig;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.OtpService;
import com.example.demo.Service.TokenBlacklistService;
import com.example.demo.Service.UserSerVice;
import com.example.demo.ServiceImpl.UserDetailService;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailService userDetailsService;
    private final UserSerVice userSerVice;
    private final TokenBlacklistService tokenBlacklistService;
    private final OtpService otpService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtTokenProvider jwtTokenProvider,
                          UserDetailService userDetailsService, UserSerVice userSerVice, TokenBlacklistService tokenBlacklistService, OtpService otpService) {
        this.authenticationManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.userSerVice = userSerVice;
        this.tokenBlacklistService = tokenBlacklistService;
        this.otpService = otpService;
    }



    @PostMapping("/forgetpassword")
    public ResponseEntity<?> forgetPass(@RequestBody ForgetDTO dto) throws MessagingException {
        Map<String, String> map = new HashMap<>();
        try {
            userSerVice.forGetPassword(dto);
            map.put("message", "Mật khẩu đã gửi về email của bạn");
            map.put("code", String.valueOf(HttpServletResponse.SC_OK));
            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            map.put("error", e.getMessage());
            map.put("code", String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
            return ResponseEntity.badRequest().body(map);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO request) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            userSerVice.registerUser(user);
            map.put("status", EnumConfig.SUCCESS.getText());
            map.put("Code", HttpServletResponse.SC_OK);
            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            map.put("status", EnumConfig.FAIL.getText());
            map.put("Code", HttpServletResponse.SC_BAD_REQUEST);
            map.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(map);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUsers() {
        HashMap<String, Object> map = new HashMap<>();
        List<User> users = userSerVice.getAllUsers();
        List<Map<String, Object>> list = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> hashMap = new HashMap<>();  // Tạo mới map mỗi lần
            hashMap.put("id", user.getId());
            hashMap.put("username", user.getUsername());
            hashMap.put("role", user.getRole());
            hashMap.put("email", user.getEmail());
            String status;
            if ("1".equals(user.isEnabled())) {
                status = "unlocked";
            } else {
                status = "locked";
            }
            hashMap.put("status", status);
            list.add(hashMap);
        }
        map.put("status", EnumConfig.SUCCESS.getText());
        map.put("Code", HttpServletResponse.SC_OK);
        map.put("data", list);
        return ResponseEntity.ok(map);

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            LocalDateTime expiry = jwtTokenProvider.getExpirationDate(token);
            tokenBlacklistService.blacklistToken(token, expiry);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("status", EnumConfig.SUCCESS.getText());
        map.put("code", HttpServletResponse.SC_OK);
        map.put("message", EnumConfig.LOGOUT_MESSAGE.getText());
        return ResponseEntity.ok(map);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/lockAndUnlock")
    public ResponseEntity<?> lockAndUnlock(@RequestParam Long id, @RequestParam Boolean enable) {
        userSerVice.SaveOrUpdate(id, enable);
        Map<String, Object> map = new HashMap<>();
        if (enable) {
            map.put("status", EnumConfig.SUCCESS.getText());
            map.put("code", HttpServletResponse.SC_OK);
            map.put("message", EnumConfig.LOCK);
        } else {
            map.put("status", EnumConfig.SUCCESS.getText());
            map.put("code", HttpServletResponse.SC_OK);
            map.put("message", EnumConfig.UNLOCK);
        }
        return ResponseEntity.ok(map);
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String message = userSerVice.changePassword(request);
        Map<String, Object> map = new HashMap<>();
        map.put("status", EnumConfig.SUCCESS.getText());
        map.put("code", HttpServletResponse.SC_OK);
        map.put("message", message);
        return ResponseEntity.ok(map);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changePassUser")
    public ResponseEntity<?> changePassUser(@RequestBody ChangePassSetRoleAdmin request) {
        String message = userSerVice.changePasswordByUserName(request);
        Map<String, Object> map = new HashMap<>();
        map.put("status", EnumConfig.SUCCESS.getText());
        map.put("code", HttpServletResponse.SC_OK);
        map.put("message", message);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<?> verifyLoginOtp(
            @RequestBody VerifyOtpDTO request
    ) {

        Map<String, Object> map =
                new HashMap<>();

        try {

            User user =
                    userRepository
                            .findByUsername(
                                    request.getUsername()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found"
                                    ));

            otpService.verifyOtp(
                    user.getEmail(),
                    request.getOtp()
            );
            UserDetails userDetails =
                    userSerVice
                            .loadUserByUsername(
                                    user.getUsername()
                            );

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            String token =
                    jwtTokenProvider.generateToken(
                            String.valueOf(authentication)
                    );

            map.put("message", "Success");
            map.put("token", token);
            map.put(
                    "code",
                    HttpServletResponse.SC_OK
            );

            return ResponseEntity.ok(map);

        } catch (Exception e) {

            map.put(
                    "error",
                    EnumConfig.ERROR.getText()
            );

            map.put(
                    "message",
                    e.getMessage()
            );

            map.put(
                    "code",
                    HttpServletResponse.SC_BAD_REQUEST
            );

            return ResponseEntity
                    .badRequest()
                    .body(map);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        Map<String, String> map = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
            User user =
                    userRepository
                            .findByUsername(
                                    request.getUsername()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found"
                                    ));

            // check user có bật OTP không
            if (Boolean.TRUE.equals(
                    user.getRequireOtp()
            )) {
                // gửi OTP login
                userSerVice.sendOtp(
                        user.getEmail() , user.getUsername()
                );
                map.put(
                        "message",
                        "OTP đã gửi về email"
                );
                map.put(
                        "requireOtp",
                        String.valueOf(true)
                );
                map.put(
                        "code",
                        String.valueOf(HttpServletResponse.SC_OK)
                );

                return ResponseEntity.ok(
                        map
                );
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(String.valueOf(authentication));
            map.put("error", "Success");
            map.put("code", String.valueOf(HttpServletResponse.SC_OK));
            map.put("token", token);
            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            map.put("error", EnumConfig.ERROR.getText());
            map.put("message", e.getMessage());
            map.put("code", String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
            return ResponseEntity.badRequest().body(map);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}

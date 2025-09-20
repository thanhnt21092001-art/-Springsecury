package com.example.demo.Controller;

import com.example.demo.Entities.User;
import com.example.demo.Service.TokenBlacklistService;
import com.example.demo.Service.UserSerVice;
import com.example.demo.ServiceImpl.UserDetailService;
import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.ChangePassSetRoleAdmin;
import com.example.demo.dto.ChangePasswordRequest;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtTokenProvider jwtTokenProvider,
                          UserDetailService userDetailsService, UserSerVice userSerVice, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.userSerVice = userSerVice;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {
        Map<String, String> map = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(String.valueOf(authentication));
            map.put("error", "Success");
            map.put("code", String.valueOf(HttpServletResponse.SC_OK));
            map.put("token", token);
            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            map.put("error", "fail");
            map.put("message", e.getMessage());
            map.put("code", String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
            return ResponseEntity.badRequest().body(map);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User request) {
        HashMap<Object, String> map = new HashMap<>();
        try {
            userSerVice.registerUser(request);
            map.put("status", "success");
            map.put("Code", String.valueOf(HttpServletResponse.SC_OK));
            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            map.put("status", "fail");
            map.put("Code", String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
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
            list.add(hashMap);
        }
        map.put("status", "success");
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
        map.put("status", "success");
        map.put("code", HttpServletResponse.SC_OK);
        map.put("message", "Đăng xuất thành công");
        return ResponseEntity.ok(map);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/lockAndUnlock")
    public ResponseEntity<?> lockAndUnlock(@RequestParam Long id, @RequestParam Boolean enalbe) {
        userSerVice.SaveOrUpdate(id, enalbe);
        Map<String, Object> map = new HashMap<>();
        if (enalbe) {
            map.put("status", "success");
            map.put("code", HttpServletResponse.SC_OK);
            map.put("message", "Mở khoá thành công");
        } else {
            map.put("status", "success");
            map.put("code", HttpServletResponse.SC_OK);
            map.put("message", "Khóa thành công");
        }
        return ResponseEntity.ok(map);
    }


    @PostMapping ("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String message = userSerVice.changePassword(request);
        Map<String, Object> map = new HashMap<>();
        map.put("status", "success");
        map.put("code", HttpServletResponse.SC_OK);
        map.put("message", message);
        return ResponseEntity.ok(map);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping ("/changePassUser")
    public ResponseEntity<?> changePassUser(@RequestBody ChangePassSetRoleAdmin request) {
        String message = userSerVice.changePasswordByUserName(request);
        Map<String, Object> map = new HashMap<>();
        map.put("status", "success");
        map.put("code", HttpServletResponse.SC_OK);
        map.put("message", message);
        return ResponseEntity.ok(map);
    }

}

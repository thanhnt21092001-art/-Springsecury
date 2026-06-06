package com.example.demo.config;

import com.example.demo.Enum.EnumConfig;
import com.example.demo.Service.TokenBlacklistService;
import com.example.demo.ServiceImpl.UserDetailService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailService userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // REST API nên disable CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgetpassword").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/lockAndUnlock").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/auth/getAll").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/auth/changePassUser").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/auth/logout").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/auth/change-password").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        // Thêm filter JWT trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, tokenBlacklistService),
                UsernamePasswordAuthenticationFilter.class
        ).exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
                        response.setContentType("application/json;charset=UTF-8");
                        Map<String, Object> map = new HashMap<>();
                        map.put("code", HttpServletResponse.SC_NOT_FOUND);
                        map.put("status", EnumConfig.ERROR);
                        map.put("message","Not found");
                        ObjectMapper mapper = new ObjectMapper();
                        response.getWriter().write(mapper.writeValueAsString(map));
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                })
        );

        return http.build();
    }

    // Dùng để authenticate khi login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}

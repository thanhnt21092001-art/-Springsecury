package com.example.demo.config;

import com.example.demo.Service.TokenBlacklistService;
import com.example.demo.ServiceImpl.UserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtTokenProvider provider, UserDetailService userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenProvider = provider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy token từ header
        String token = getTokenFromRequest(request);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    handleUnauthorizedResponse(response, "Token đã bị thu hồi. Vui lòng đăng nhập lại");
                    return;
                }
                String input = jwtTokenProvider.getUsernameFromToken(token);
                String username = getUserNameData(input);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                handleUnauthorizedResponse(response, "Invalid or missing token");
            }
        } catch (RuntimeException e) {
            handleUnauthorizedResponse(response, "Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            handleInternalServerError(response,e.getMessage());
        }

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void handleUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("code", "401");
        error.put("message", message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(error));
    }

    private void handleInternalServerError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("code", "500");
        error.put("message", message);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(error));
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

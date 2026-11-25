package com.example.demo.ServiceImpl;

import com.example.demo.Entities.User;
import com.example.demo.Enum.EnumConfig;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountExpiredException;
import java.util.Date;


@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    public UserDetailService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("".equals(username) || username == null) {
            throw new RuntimeException(EnumConfig.VALIDATE.getText());
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(EnumConfig.NOT_FOUND_USER.getText()));

        if (user.getDate_end() != null && user.getDate_end().before(new Date())) {
            try {
                throw new AccountExpiredException(EnumConfig.USER_EXPIRED.getText());
            } catch (AccountExpiredException e) {
                throw new RuntimeException(e);
            }
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // password phải được mã hóa (BCrypt)
                .roles(user.getRole()) // giả sử user có trường role là String
                .build();
    }


}

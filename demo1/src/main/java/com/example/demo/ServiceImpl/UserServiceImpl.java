package com.example.demo.ServiceImpl;

import com.example.demo.Entities.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserSerVice;
import com.example.demo.dto.ChangePassSetRoleAdmin;
import com.example.demo.dto.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserSerVice {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(User request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setCreate_date(new Date(System.currentTimeMillis()));
        user.setDate_end(Date.valueOf(LocalDate.now().plusDays(30)));
        user.setEnabled(true);// default role
        return userRepository.save(user);
    }


    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void SaveOrUpdate(Long id, Boolean enalbe) {
        userRepository.updateUserEnabledStatus(id, enalbe);
    }

    @Override
    public String changePassword(ChangePasswordRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Đổi mật khẩu thất bại");
        }

        // Đổi mật khẩu và lưu lại
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Thay đổi mật khẩu thành công";
    }

    @Override
    public String changePasswordByUserName(ChangePassSetRoleAdmin request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Thay đổi mật khẩu thành công";
    }


}

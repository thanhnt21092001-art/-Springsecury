package com.example.demo.ServiceImpl;

import com.example.demo.Entities.OtpEntity;
import com.example.demo.Entities.User;
import com.example.demo.Enum.EnumConfig;
import com.example.demo.Repository.OtpRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.UserSerVice;
import com.example.demo.dto.ChangePassSetRoleAdmin;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.ForgetDTO;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserSerVice {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpRepository  otpRepository;

    @Value("${keypass}")
    private String keyPassword;

    public void UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(User request) {
        if ("".equals(request.getPassword()) || "".equals(request.getUsername())) {
            throw new RuntimeException(EnumConfig.INVALID_USER_PASS.getText());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(EnumConfig.NOT_FOUND_USER.getText());
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(EnumConfig.ROLE_USER.getText());
        user.setCreate_date(new Date(System.currentTimeMillis()));
        user.setDate_end(Date.valueOf(LocalDate.now().plusDays(30)));
        user.setEnabled(true);// default role
        user.setRequireOtp(false);
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
            throw new RuntimeException(EnumConfig.NOT_FOUND_USER.getText());
        }

        User user = optionalUser.get();

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException(EnumConfig.CHANGE_ERROR.getText());
        }

        // Đổi mật khẩu và lưu lại
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return EnumConfig.PASSWORD_CHANGE_SUCCESS.getText();
    }

    @Override
    public String forGetPassword(ForgetDTO request) throws MessagingException {
        String username = request.getUsername();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException(EnumConfig.NOT_FOUND_USER.getText());
        }

        User user = optionalUser.get();
        if (optionalUser.isEmpty()) {
            throw new RuntimeException(EnumConfig.NOT_FOUND_USER.getText());
        }
        if (user.getDate_end() != null && user.getDate_end().before(new java.util.Date())) {
            throw new RuntimeException(EnumConfig.USER_EXPIRED.getText());
        }
        Boolean checkLockUnlock = user.isEnabled();
        if (!checkLockUnlock) {
            throw new RuntimeException(EnumConfig.TOKEN_RECALL.getText());
        }
        String email = user.getEmail();
        // generate password mới random
        String newPassword = generateRandomPassword();

        // encode password
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        sendPasswordEmail(username, email, newPassword);
        // gửi email
        userRepository.save(user);
        return EnumConfig.PASSWORD_CHANGE_SUCCESS.getText();
    }


    private void sendPasswordEmail(String userName, String email, String newPassword) throws MessagingException {
        String html = """
                <html>
                    <body style="font-family: Arial, sans-serif;">
                        <h2>Xin chào %s,</h2>
                
                        <p>Mật khẩu mới của quý khách là:</p>
                
                        <div style="
                            background-color:#f4f4f4;
                            padding:15px;
                            border-radius:8px;
                            font-size:20px;
                            font-weight:bold;
                            width:fit-content;">
                            %s
                        </div>
                
                        <p>Vui lòng đăng nhập và đổi mật khẩu ngay để đảm bảo an toàn.</p>
                    </body>
                </html>
                """.formatted(userName, newPassword);
        emailService.sendHtmlMail(email, "", html);


    }

    private String generateRandomPassword() {
        String chars = keyPassword;

        StringBuilder password = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    @Override
    public String changePasswordByUserName(ChangePassSetRoleAdmin request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return EnumConfig.PASSWORD_CHANGE_SUCCESS.getText();
    }

    private String generateOtp() {
        String chars = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(
                    chars.charAt(
                            random.nextInt(
                                    chars.length()
                            )
                    )
            );
        }
        return otp.toString();
    }

    public void sendOtp(String email, String userName) throws MessagingException {
        String otp = generateOtp();
        sendOtpData(email,otp);
        String html = """
                <html>
                    <body style="font-family: Arial, sans-serif;">
                        <h2>Xin chào %s,</h2>
                
                        <p>Mã xác thực của bạn là :</p>
                
                        <div style="
                            background-color:#f4f4f4;
                            padding:15px;
                            border-radius:8px;
                            font-size:20px;
                            font-weight:bold;
                            width:fit-content;">
                            %s
                        </div>
                    </body>
                </html>
                """.formatted(userName, otp);
        emailService.sendHtmlMail(email, "", html);


    }

    @Transactional
    private void sendOtpData(
            String email ,String otp
    ) throws MessagingException {

        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiredAt(
                LocalDateTime.now()
                        .plusMinutes(5)
        );

        otpEntity.setUsed(false);
        otpRepository.save(otpEntity);
    }

    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        User user =
                userRepository
                        .findByUsername(
                                username
                        )
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found"
                                ));

        List<GrantedAuthority>
                authorities =
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" +
                                        user.getRole()
                        )
                );

        return new org.springframework
                .security
                .core
                .userdetails
                .User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}

package com.example.demo.ServiceImpl;

import com.example.demo.Entities.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserSerVice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserSerVice {
    @Autowired
    private UserRepository userRepository;
  @Autowired
    private  PasswordEncoder passwordEncoder;

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
        user.setEnabled(true);// default role
        return userRepository.save(user);
    }



    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void SaveOrUpdate(Long id , Boolean enalbe) {
        userRepository.updateUserEnabledStatus(id ,enalbe);
    }


}

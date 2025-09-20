package com.example.demo.Service;

import com.example.demo.Entities.User;
import com.example.demo.dto.ChangePassSetRoleAdmin;
import com.example.demo.dto.ChangePasswordRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserSerVice  {
    public List<User> getAllUsers();
    public User saveUser(User username);
    User registerUser(User request);
    void SaveOrUpdate(Long id , Boolean enalbe);
    String changePassword(ChangePasswordRequest request);

    String changePasswordByUserName(ChangePassSetRoleAdmin request);
}

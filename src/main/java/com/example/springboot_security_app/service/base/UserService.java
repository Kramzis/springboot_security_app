package com.example.springboot_security_app.service.base;

import com.example.springboot_security_app.dto.UserDTO;
import com.example.springboot_security_app.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    User getUserById(Integer userId);
    void saveUser(UserDTO userDto);
    User updateUser(Integer userId, User updatedUser);
    User deleteUserById(Integer userId);
    User getCurrentUser();
}

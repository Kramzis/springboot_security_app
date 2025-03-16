package com.example.springboot_security_app.service.base;

import com.example.springboot_security_app.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    void saveUser(UserDTO userDto);
}

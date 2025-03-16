package com.example.springboot_security_app.service.base;

import com.example.springboot_security_app.entity.Role;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
    Role getRoleByName(String name);
}

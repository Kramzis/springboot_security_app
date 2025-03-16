package com.example.springboot_security_app.service;

import com.example.springboot_security_app.entity.Role;
import com.example.springboot_security_app.repository.RoleRepository;
import com.example.springboot_security_app.service.base.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleByName(String name) {
        Role newRole = roleRepository.findByName(name);
        if (newRole == null) {
            return roleRepository.save(new Role(null, name, null));
        } else {
            return newRole;
        }
    }
}

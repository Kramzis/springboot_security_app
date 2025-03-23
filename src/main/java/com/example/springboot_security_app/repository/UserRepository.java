package com.example.springboot_security_app.repository;

import com.example.springboot_security_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserById(Integer userId);
    User findByUsername(String username);
    User findByEmail(String email);
}

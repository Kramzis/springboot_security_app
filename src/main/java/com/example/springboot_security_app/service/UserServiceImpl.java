package com.example.springboot_security_app.service;

import com.example.springboot_security_app.dto.UserDTO;
import com.example.springboot_security_app.entity.Post;
import com.example.springboot_security_app.entity.Role;
import com.example.springboot_security_app.entity.User;
import com.example.springboot_security_app.repository.UserRepository;
import com.example.springboot_security_app.service.base.RoleService;
import com.example.springboot_security_app.service.base.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleServiceImpl;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleServiceImpl, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleServiceImpl = roleServiceImpl;
        this.passwordEncoder = passwordEncoder;}

    @Override
    public User getUserById(Integer id) {
        User user = userRepository.getUserById(id);
        if(user.getDeletedAt() != null){
            return null;
        } else {
            return user;
        }
    }

    @Override
    public void saveUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already taken");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleServiceImpl.getRoleByName(Role.ROLE_USER);
        user.setRoles(Arrays.asList(role));

        userRepository.save(user);
    }

    @Override
    public void updateUser(Integer userId, User updatedUser) {
        User user = userRepository.findById(userId).orElse(null);
        if(user != null) {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public void deleteUserById(Integer userId){
        User user = userRepository.findById(userId).orElse(null);
        if(user != null) {
            user.setDeletedAt(LocalDate.now());
            for(Post post : user.getPosts()){
                post.setDeletedAt(LocalDate.now());
            }
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            User user = userRepository.findByEmail(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            return user;
        } else {
             throw new RuntimeException("No authenticated user found");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail ( email );
        if(user == null || user.getDeletedAt() != null){
            throw new UsernameNotFoundException ( "User not found" );
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail (),user.getPassword (), mapRolesToAuthorities ( user.getRoles () ));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
        return roles.stream ().map ( role -> new SimpleGrantedAuthority( role.getName () ) ).collect ( Collectors.toList (  ) );
    }

}


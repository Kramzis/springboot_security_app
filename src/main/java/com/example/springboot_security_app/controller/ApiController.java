package com.example.springboot_security_app.controller;

import com.example.springboot_security_app.configuration.JwtService;
import com.example.springboot_security_app.dto.LoginDTO;
import com.example.springboot_security_app.dto.PostDTO;
import com.example.springboot_security_app.entity.Post;
import com.example.springboot_security_app.service.base.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RestController
@RequestMapping("/api")
public class ApiController {
    private final PostService postService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ApiController(PostService postService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.postService = postService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/login")
    public String authenticate(@RequestBody LoginDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getEmail());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDTO postDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCreatedAt(LocalDate.now());

        postService.createPost(post);
        return ResponseEntity.status(201).body("Post created successfully");
    }
}

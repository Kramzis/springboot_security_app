package com.example.springboot_security_app.controller;

import com.example.springboot_security_app.dto.PostDTO;
import com.example.springboot_security_app.entity.Post;
import com.example.springboot_security_app.repository.UserRepository;
import com.example.springboot_security_app.service.base.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RestController
@RequestMapping("/api")
public class ApiController {
    private final PostService postService;
    private final UserRepository userRepository;

    public ApiController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllActivePosts().stream()
                .map(post -> new PostDTO(post.getTitle(), post.getContent(), post.getCreatedAt(), post.getCreatedBy()))
                .collect(Collectors.toList());
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

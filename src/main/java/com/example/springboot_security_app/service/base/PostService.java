package com.example.springboot_security_app.service.base;

import com.example.springboot_security_app.entity.Post;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PostService {
    Post getPostById(Integer id);
    Post createPost(Post post);
    Post updatePost(Integer postId, Post post);
    Post deletePostById(Integer postId);
    List<Post> getAllActivePosts();
}

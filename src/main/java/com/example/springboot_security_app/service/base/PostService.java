package com.example.springboot_security_app.service.base;

import com.example.springboot_security_app.dto.PostDTO;
import com.example.springboot_security_app.entity.Post;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {
    Post getPostById(Integer id);
    void createPost(Post post);
    void updatePost(Integer postId, Post post);
    void deletePostById(Integer postId);
    List<Post> getAllActivePosts();
    List<PostDTO> getAllPosts();
}

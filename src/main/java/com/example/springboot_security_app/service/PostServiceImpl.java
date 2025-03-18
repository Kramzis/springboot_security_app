package com.example.springboot_security_app.service;

import com.example.springboot_security_app.entity.Post;
import com.example.springboot_security_app.repository.PostRepository;
import com.example.springboot_security_app.repository.UserRepository;
import com.example.springboot_security_app.service.base.PostService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    PostRepository postRepository;
    UserRepository userRepository;

    PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Post getPostById(Integer id) {
        Post post = postRepository.findPostById(id);
        if(post.getDeletedAt() != null){
            return null;
        } else {
            return post;
        }
    }

    @Override
    public Post createPost(Post post){
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Integer postId, Post newPost) {
        Post post = postRepository.findPostById(postId);
        if(post != null){
            post.setTitle(newPost.getTitle());
            post.setContent(newPost.getContent());
            post.setCreatedAt(LocalDate.now());

            return postRepository.save(post);
        } else {
            return null;
        }
    }

    @Override
    public Post deletePostById(Integer postId) {
        Post post = postRepository.findPostById(postId);
        post.setDeletedAt(LocalDate.now());

        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllActivePosts() {
        return postRepository.findByDeletedAtIsNull();
    }
}

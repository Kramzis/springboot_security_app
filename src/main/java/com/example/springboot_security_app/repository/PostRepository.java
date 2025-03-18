package com.example.springboot_security_app.repository;

import com.example.springboot_security_app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findPostById(Integer id);
    List<Post> findByDeletedAtIsNull();
}

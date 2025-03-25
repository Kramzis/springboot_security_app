package com.example.springboot_security_app.repository;

import com.example.springboot_security_app.dto.PostDTO;
import com.example.springboot_security_app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findPostById(Integer id);
    List<Post> findByDeletedAtIsNull();
    @Query("SELECT new com.example.springboot_security_app.dto.PostDTO(p.title, p.content, p.createdAt, p.createdBy) FROM Post p WHERE p.deletedAt IS NULL")
    List<PostDTO> findPostsByDeletedAtIsNull();

}

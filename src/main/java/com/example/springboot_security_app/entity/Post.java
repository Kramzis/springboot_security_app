package com.example.springboot_security_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User createdBy;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = true)
    private LocalDate deletedAt = null;

    public Post() {
    }

    public Post(Integer id, String title, String content, User createdBy, LocalDate createdAt, LocalDate deletedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public void setId(Integer id) { this.id = id; }

    public Integer getId() { return id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public User getCreatedBy() { return createdBy; }

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDate getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getDeletedAt() { return deletedAt; }

    public void setDeletedAt(LocalDate deletedAt) { this.deletedAt = deletedAt; }
}

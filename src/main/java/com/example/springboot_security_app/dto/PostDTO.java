package com.example.springboot_security_app.dto;

import com.example.springboot_security_app.entity.User;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class PostDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private LocalDate createdAt = LocalDate.now();

    private User createdBy;

    public PostDTO() {}

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public LocalDate getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public User getCreatedBy() { return createdBy; }

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}

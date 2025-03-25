package com.example.springboot_security_app.controller;

import com.example.springboot_security_app.dto.PostDTO;
import com.example.springboot_security_app.entity.Post;
import com.example.springboot_security_app.entity.User;
import com.example.springboot_security_app.service.base.PostService;
import com.example.springboot_security_app.service.base.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/home")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping()
    public String getPosts(Model model) {
        User user = userService.getCurrentUser();
        List<PostDTO> listOfPosts = postService.getAllPosts();
        model.addAttribute("posts", listOfPosts);
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/newPost")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new PostDTO());
        return "add_post";
    }

    @PostMapping("/newPost")
    public String createPost(@Valid PostDTO postDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "add_post";
        }

        ModelMapper modelMapper = new ModelMapper();
        Post post = modelMapper.map(postDTO, Post.class);

        User user = userService.getCurrentUser();
        post.setCreatedBy(user);
        postService.createPost(post);

        redirectAttributes.addFlashAttribute("message", "Post added successfully!");
        return "redirect:/home";
    }


    @GetMapping("/editPost/{postId}")
    public String showEditPostForm(@PathVariable Integer postId, Model model) {
        Post post = postService.getPostById(postId);
        ModelMapper modelMapper = new ModelMapper();
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
        model.addAttribute("post", postDTO);
        model.addAttribute("postId", postId);
        return "edit_post";
    }


    @PostMapping("/editPost/{postId}")
    public String updatePost(@PathVariable Integer postId,
                             @Valid @ModelAttribute("postDTO") PostDTO postDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "edit_post";
        }

        ModelMapper modelMapper = new ModelMapper();
        Post updatedPost = modelMapper.map(postDTO, Post.class);
        postService.updatePost(postId, updatedPost);

        redirectAttributes.addFlashAttribute("message", "Post updated successfully!");
        return "redirect:/home";
    }


    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable Integer postId, RedirectAttributes redirectAttributes) {
        try {
            postService.deletePostById(postId);
            redirectAttributes.addFlashAttribute("message", "Post deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Post could not be deleted!");
        }
        return "redirect:/home";
    }

}

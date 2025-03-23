package com.example.springboot_security_app.controller;

import com.example.springboot_security_app.dto.UserDTO;
import com.example.springboot_security_app.entity.User;
import com.example.springboot_security_app.service.base.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/editUser/{userId}")
    public String showEditUserForm(@PathVariable Integer userId, Model model) {
        User user = userService.getUserById(userId);
        ModelMapper modelMapper = new ModelMapper();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("user", userDTO);
        model.addAttribute("userId", userId);
        return "edit_user";
    }


    @PostMapping("/editUser/{userId}")
    public String updateUser(@PathVariable Integer userId,
                             @Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "edit_user";
        }
        userDTO.setUserId(userId);
        ModelMapper modelMapper = new ModelMapper();
        User updatedUser = modelMapper.map(userDTO, User.class);
        userService.updateUser(userId, updatedUser);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/logout";
    }


    @PostMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable Integer userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(userId);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "User could not be deleted!");
        }
        return "redirect:/logout";
    }
}

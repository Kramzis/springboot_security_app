package com.example.springboot_security_app.controller;


import com.example.springboot_security_app.dto.LoginDTO;
import com.example.springboot_security_app.dto.UserDTO;
import com.example.springboot_security_app.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/")
public class AuthController {

    private final UserServiceImpl userServiceImpl;

    public AuthController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/signin")
    public String showSigninForm(Model model) {
        model.addAttribute("login", new LoginDTO());
        return "signin";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInUser");
        return "redirect:/signin";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignUp(@ModelAttribute("user") @Valid UserDTO userDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "signup";
        }

        try {
            userServiceImpl.saveUser(userDTO);
            redirectAttributes.addFlashAttribute("success", "Registration successful");
            return "redirect:/signin";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.user", e.getMessage());
            return "signup";
        }
    }

}

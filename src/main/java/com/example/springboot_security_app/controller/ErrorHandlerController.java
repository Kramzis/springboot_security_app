package com.example.springboot_security_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorHandlerController {

    @RequestMapping("/error_403")
    public String handleAccessDeniedError() {
        return "error_403";
    }

}


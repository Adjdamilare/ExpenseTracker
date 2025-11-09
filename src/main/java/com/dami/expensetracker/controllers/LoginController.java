package com.dami.expensetracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        // Add an error message to the model if login failed
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }

        // Add a logout message to the model if the user just logged out
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        return "login"; // Return the name of the HTML template
    }
}
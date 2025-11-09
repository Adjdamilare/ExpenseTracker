package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.RegisterDto;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant; // 1. Add this import

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Handles GET requests to /register to show the registration form.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Provide an empty DTO to bind the form data to
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    /**
     * Handles POST requests to /register to process the form submission.
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerDto") RegisterDto registerDto,
            BindingResult result,
            Model model) {

        // Custom validation: Check if passwords match
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "null", "Passwords do not match");
        }

        // Custom validation: Check if email is already in use
        if (userRepository.findByEmail(registerDto.getEmail()) != null) {
            result.rejectValue("email", "null", "An account with this email already exists");
        }

        // If there are any validation errors, return to the form
        if (result.hasErrors()) {
            return "register";
        }

        // If validation passes, create and save the new user
        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setCreatedAt(Instant.now()); // 2. Set the creation timestamp here

        userRepository.save(newUser);

        // Add a success flag to the model to show a success message on the page
        model.addAttribute("success", true);
        // Return to the same page to display the success message
        return "register";
    }
}
package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; // 1. Add @Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring MVC controller for handling account-related operations.
 * This controller manages user profile functionality.
 */
@Controller // 2. Make this a Spring Controller
public class AccountController {

    /**
     * Repository for accessing user data in the database.
     * Using constructor injection for better testability and immutability.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new AccountController with the required UserRepository.
     *
    // * @param  repository to be injected for user data access
     */
    @Autowired // 3. Inject the repository via the constructor
    public AccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Handles GET requests to the /profile endpoint.
     * Retrieves the currently authenticated user's profile information.
     *
     * @param model the Spring Model to add attributes for the view
     * @return the name of the profile view or a redirect to login if user not found
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        // Get the currently authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Find user by email in the repository
        User user = userRepository.findByEmail(email);

        // If user not found, redirect to login with error message
        if (user == null) {
            return "redirect:/login?error=UserNotFound";
        }

        // Add user to model for view rendering
        model.addAttribute("user", user);
        return "profile";
    }
}

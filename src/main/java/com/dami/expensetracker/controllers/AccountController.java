package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; // 1. Add @Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 2. Make this a Spring Controller
public class AccountController {

    private final UserRepository userRepository;

    @Autowired // 3. Inject the repository via the constructor
    public AccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // Get the currently authenticated user's name (which is their email)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            // This is a safety check in case the user was deleted from the DB
            // while their session was still active.
            return "redirect:/login?error=UserNotFound";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    // The redundant registration methods have been removed, as they are correctly
    // handled by your RegistrationController.
}



//package com.dami.expensetracker.controllers;
//
//import com.dami.expensetracker.models.RegisterDto;
//import com.dami.expensetracker.models.User;
//import com.dami.expensetracker.repositories.UserRepository;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import java.util.Date;
//
//@Controller
//public class AccountController {
//
//    private UserRepository repo;
//
//
//    private final UserRepository userRepository;
//
//    @Autowired // 3. Inject the repository via the constructor
//    public AccountController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @GetMapping("/register")
//    public String register(Model model) {
//        RegisterDto registerDto = new RegisterDto();
//        model.addAttribute("registerDto", registerDto);
//        model.addAttribute("success", false);
//        return "register";
//    }
//
//    @GetMapping("/profile")
//    public String profile(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
//            // If user is not authenticated, redirect to login
//            return "redirect:/login";
//        }
//
//        String email;
//        Object principal = authentication.getPrincipal();
//
//        if (principal instanceof UserDetails) {
//            email = ((UserDetails) principal).getUsername();
//        } else {
//            email = principal.toString();
//        }
//
//        User user = repo.findByEmail(email);
//
//        if (user == null) {
//            model.addAttribute("error", "User profile not found.");
//        }
//
//        model.addAttribute("user", user);
//        return "profile";
//    }
//
//    @PostMapping("/register")
//    public String register(Model model, @Valid @ModelAttribute RegisterDto registerDto, BindingResult result) {
//        if(!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
//            result.addError(new FieldError("registerDto", "confirmPassword", "Password and Confirm Password must match"));
//        }
//
//        User appUser = repo.findByEmail(registerDto.getEmail());
////        System.out.println("AppUser: " + appUser.toString());
//        if(appUser != null) {
//            result.addError(new FieldError("registerDto", "email", "Email Address already exists"));
//        }
//        if(result.hasErrors()) {
//            return "register";
//        }
//        try {
//            var bCryptPasswordEncoder = new BCryptPasswordEncoder();
//
//            User user = new User();
//            user.setEmail(registerDto.getEmail());
//            user.setUsername(registerDto.getUsername());
//            user.setPasswordHash(bCryptPasswordEncoder.encode(registerDto.getPassword()));
//            user.setCreatedAt(new Date().toInstant());
//            repo.save(user);
//
//            model.addAttribute("registerDto", new RegisterDto());
//            model.addAttribute("success", true);
//        }
//        catch (Exception e) {
//            result.addError(new FieldError("registerDto", "firstName", e.getMessage()));
//        }
//        return "register";
//    }
//}

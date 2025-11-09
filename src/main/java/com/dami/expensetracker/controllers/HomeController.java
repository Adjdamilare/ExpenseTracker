package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; // 1. Import the a
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 2. Add the annotation here
public class HomeController {
    @Autowired
    UserRepository appUserRepository;

    @GetMapping({"", "/"})
    public String home(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = appUserRepository.findByEmail(email);

            if (user == null) {
                return "redirect:/login";
            }

        } else {
            model.addAttribute("guestMessage", "Welcome Guest!.");
        }
        return "index";
    }



    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }
}

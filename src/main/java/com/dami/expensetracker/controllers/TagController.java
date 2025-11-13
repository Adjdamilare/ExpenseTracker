package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Tag;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.services.TagService;
import com.dami.expensetracker.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class TagController {

    private final TagService tagService;
    private final UserService userService;

    @Autowired
    public TagController(TagService tagService, UserService userService) {
        this.tagService = tagService;
        this.userService = userService;
    }

    /**
     * Displays the page for adding and viewing tags.
     */
    @GetMapping("/addTags")
    public String showAddTagsForm(Model model, Principal principal) {
        // Get the currently logged-in user
        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        // Add the user's existing tags to the model to display them
        model.addAttribute("tags", tagService.findByUser(currentUser));
        // Add a new, empty Tag object for the form to bind to
        model.addAttribute("newTag", new Tag());

        return "add-tags"; // The name of our new HTML template
    }

    /**
     * Processes the submission of the new tag form.
     */
    @PostMapping("/addTags")
    public String addTag(@Valid @ModelAttribute("newTag") Tag newTag,
                         BindingResult result,
                         Principal principal,
                         Model model) {

        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        // Optional but recommended: Check for duplicate tag names
        if (tagService.existsByNameAndUser(newTag.getName(), currentUser)) {
            result.rejectValue("name", "tag.duplicate", "You already have a tag with this name.");
        }

        // If the form has validation errors (e.g., empty name or duplicate), return to the form
        if (result.hasErrors()) {
            // We must repopulate the list of existing tags before returning
            model.addAttribute("tags", tagService.findByUser(currentUser));
            return "add-tags";
        }

        // Associate the new tag with the current user
        newTag.setUser(currentUser);
        tagService.save(newTag);

        // Redirect back to the same page to show the newly added tag
        return "redirect:/addTags";
    }

    @GetMapping("/tags")
    public String listTags(Model model, Principal principal) {
        // 1. Get the username of the currently logged-in user
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found: " + username));

        // 2. Fetch all tags for that user using the TagService
        List<Tag> userTags = tagService.findByUser(currentUser);

        // 3. Add the list of tags to the model, making it available to Thymeleaf
        model.addAttribute("tags", userTags);

        // 4. Return the name of the view template
        return "tags";
    }
}
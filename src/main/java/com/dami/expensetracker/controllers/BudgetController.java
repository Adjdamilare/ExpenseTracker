// In com/dami/expensetracker/controllers/BudgetController.java

package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Budget;
import com.dami.expensetracker.models.Tag;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.services.BudgetService;
import com.dami.expensetracker.services.TagService;
import com.dami.expensetracker.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.beans.PropertyEditorSupport;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class BudgetController {

    private final BudgetService budgetService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    public BudgetController(BudgetService budgetService, TagService tagService, UserService userService) {
        this.budgetService = budgetService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Tag.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                    return; // Exit early
                }
                try {
                    // Use the TagService to find the Tag by its ID
                    setValue(tagService.findById(Integer.parseInt(text)).orElse(null));
                } catch (NumberFormatException e) {
                    // If text is not a valid number, this prevents a crash.
                    // The @Valid annotation will likely catch the resulting null value.
                    setValue(null);
                }
            }
        });
    }

    // ... rest of your controller is correct ...
    private void loadUserTags(Model model, User user) {
        List<Tag> userTags = tagService.findByUser(user);
        model.addAttribute("tags", userTags);
    }

    @GetMapping("/addBudget")
    public String showAddBudgetForm(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!model.containsAttribute("budget")) {
            Budget budget = new Budget();
            budget.setStartDate(LocalDate.now().withDayOfMonth(1));
            budget.setEndDate(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
            model.addAttribute("budget", budget);
        }

        loadUserTags(model, currentUser);
        return "add-budget";
    }

    @PostMapping("/addBudget")
    public String addBudget(@Valid @ModelAttribute("budget") Budget budget,
                            BindingResult result,
                            Principal principal,
                            Model model) {

        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (budget.getStartDate() != null && budget.getEndDate() != null && budget.getEndDate().isBefore(budget.getStartDate())) {
            result.rejectValue("endDate", "endDate.invalid", "End date cannot be before the start date.");
        }

        // This check will now work correctly because of the equals/hashCode in User.java
        if (budget.getTag() != null && !budget.getTag().getUser().equals(currentUser)) {
            result.rejectValue("tag", "tag.invalid.user", "Invalid tag selected.");
        }

//        if (result.hasErrors()) {
//            loadUserTags(model, currentUser);
//            return "add-budget";
//        }

        budget.setUser(currentUser);
        budgetService.save(budget);

        return "redirect:/";
    }

    @GetMapping("/budgets")
    public String listBudgets(Model model, Principal principal) {
        // 1. Get the currently logged-in user
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found: " + username));

        // 2. Fetch all budgets for that user (assuming a findByUser method in your service)
        List<Budget> userBudgets = budgetService.findByUser(currentUser);

        // 3. Add the list of budgets to the model for Thymeleaf
        model.addAttribute("budgets", userBudgets);

        // 4. Return the name of the view template
        return "budgets";
    }
}
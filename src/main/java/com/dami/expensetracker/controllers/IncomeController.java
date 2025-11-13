package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Income;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.services.IncomeService;
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
import java.time.LocalDate;
import java.util.List;

@Controller
public class IncomeController {

    private final IncomeService incomeService;
    private final UserService userService;

    @Autowired
    public IncomeController(IncomeService incomeService, UserService userService) {
        this.incomeService = incomeService;
        this.userService = userService;
    }

    @GetMapping("/addIncome")
    public String showAddIncomeForm(Model model) {
        Income income = new Income();
        // Pre-populate the date with today for user convenience
        income.setIncomeDate(LocalDate.now());
        model.addAttribute("income", income);
        return "add-income"; // The name of our new HTML view file
    }

    @PostMapping("/addIncome")
    public String addIncome(@Valid @ModelAttribute("income") Income income,
                            BindingResult result,
                            Principal principal) {

        // Find the currently logged-in user
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        if (result.hasErrors()) {
            // If there are validation errors, return to the form to display them
            return "add-income";
        }

        // Associate the new income record with the current user
        income.setUser(currentUser);

        // Save the income object to the database
        incomeService.save(income);

        // Redirect to the dashboard after successful submission
        return "redirect:/";
    }

    /**
     * Handles requests to view all income entries for the current user.
     */
    @GetMapping("/incomes")
    public String listIncomes(Model model, Principal principal) {
        // 1. Get the currently logged-in user
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found: " + username));

        // 2. Fetch all income entries for that user using the IncomeService
        // Assuming IncomeService has a method like findByUser(User user)
        List<Income> userIncomes = incomeService.findByUser(currentUser);

        // 3. Add the list of incomes to the model, making it available to Thymeleaf
        model.addAttribute("incomes", userIncomes);

        // 4. Return the name of the view template
        return "incomes";
    }
}
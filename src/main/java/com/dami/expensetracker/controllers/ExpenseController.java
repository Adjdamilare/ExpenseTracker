package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Expens;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.services.ExpenseService;
import com.dami.expensetracker.services.PaymentMethodService;
import com.dami.expensetracker.services.TagService; // 1. Import TagService
import com.dami.expensetracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDate;

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final PaymentMethodService paymentMethodService;
    private final TagService tagService; // 2. Inject TagService

    @Autowired
    public ExpenseController(ExpenseService expenseService, UserService userService, PaymentMethodService paymentMethodService, TagService tagService) { // 3. Add to constructor
        this.expenseService = expenseService;
        this.userService = userService;
        this.paymentMethodService = paymentMethodService;
        this.tagService = tagService; // 4. Assign in constructor
    }

    @GetMapping("/addExpense")
    public String showAddExpenseForm(Model model, Principal principal) { // 5. Add Principal to get user
        Expens expense = new Expens();
        expense.setExpenseDate(LocalDate.now());

        // Get current user to fetch their specific tags
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found for fetching data"));

        model.addAttribute("expense", expense);
        model.addAttribute("paymentMethods", paymentMethodService.findAll());
        model.addAttribute("tags", tagService.findByUser(currentUser)); // 6. Fetch and add user's tags to the model

        return "add-expense";
    }

    @PostMapping("/addExpense")
    public String addExpense(@ModelAttribute("expense") Expens expense,
                             BindingResult result,
                             Principal principal,
                             Model model) {

        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        if (result.hasErrors()) {
            // Repopulate dynamic data if there are form errors
            model.addAttribute("paymentMethods", paymentMethodService.findAll());
            model.addAttribute("tags", tagService.findByUser(currentUser));
            return "add-expense";
        }

        // Associate the new expense with the logged-in user
        expense.setUser(currentUser);

        // --- FIX: Set the creation timestamp ---
        expense.setCreatedAt(java.time.Instant.now());

        expenseService.save(expense);

        // Redirect to a relevant page after saving
        return "redirect:/";
    }
}
package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Expens;
import com.dami.expensetracker.models.Tag;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.services.ExpenseService;
import com.dami.expensetracker.services.PaymentMethodService;
import com.dami.expensetracker.services.TagService;
import com.dami.expensetracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final PaymentMethodService paymentMethodService;
    private final TagService tagService;

    @Autowired
    public ExpenseController(ExpenseService expenseService, UserService userService, PaymentMethodService paymentMethodService, TagService tagService) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.paymentMethodService = paymentMethodService;
        this.tagService = tagService;
    }

    @GetMapping("/addExpense")
    public String showAddExpenseForm(Model model, Principal principal) {
        Expens expense = new Expens();
        expense.setExpenseDate(LocalDate.now());

        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found for fetching data"));

        model.addAttribute("expense", expense);
        model.addAttribute("paymentMethods", paymentMethodService.findAll());
        model.addAttribute("tags", tagService.findByUser(currentUser));

        return "add-expense";
    }

    @PostMapping("/addExpense")
    public String addExpense(@ModelAttribute("expense") Expens expense,
                             BindingResult result,
                             @RequestParam(name = "tagIds", required = false) List<Integer> tagIds, // Get tag IDs
                             Principal principal,
                             Model model) {

        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        if (result.hasErrors()) {
            model.addAttribute("paymentMethods", paymentMethodService.findAll());
            model.addAttribute("tags", tagService.findByUser(currentUser));
            return "add-expense";
        }

        // Associate the expense with the logged-in user
        expense.setUser(currentUser);

        // Map the selected tags to the expense using the join entity
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> selectedTags = tagService.findAllByIds(tagIds);
            for (Tag tag : selectedTags) {
                expense.addTag(tag); // Use the helper method to create the link
            }
        }

        // Save the expense. JPA will also save the associated ExpenseTag entities due to cascading.
        expenseService.save(expense);

        return "redirect:/";
    }
}
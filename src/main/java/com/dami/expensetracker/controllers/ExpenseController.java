package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Expens;
import com.dami.expensetracker.models.Tag;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.ExpenseRepository;
import com.dami.expensetracker.repositories.UserRepository;
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

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository; // You'll need this to get the User object
    private final ExpenseService expenseService;
    private final UserService userService;
    private final PaymentMethodService paymentMethodService;
    private final TagService tagService;

    @Autowired
    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository, ExpenseService expenseService, UserService userService, PaymentMethodService paymentMethodService, TagService tagService) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
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




    // ... your existing methods for adding expenses ...

    @GetMapping("/expenses")
    public String listExpenses(Model model, Principal principal) {
        // 1. Get the username of the currently logged-in user
        String username = principal.getName();

        // 2. Find the full User object from the database using the service
        // This is the corrected line. It properly unwraps the Optional<User>.
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found: " + username));

        // 3. Fetch all expenses for that user using our new repository method
        List<Expens> userExpenses = expenseRepository.findAllByUserOrderByExpenseDateDesc(currentUser);

        // 4. Add the list of expenses to the model, making it available to Thymeleaf
        model.addAttribute("expenses", userExpenses);

        // 5. Return the name of the view template
        return "expenses";
    }
}
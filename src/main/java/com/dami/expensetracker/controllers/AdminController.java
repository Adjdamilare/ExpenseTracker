package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Expens;
import com.dami.expensetracker.models.Income;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for handling administrative functions and dashboard display.
 * This controller provides various metrics and data visualizations for platform administrators.
 */
@Controller
public class AdminController {

    // Repository dependencies for data access
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final BudgetRepository budgetRepository;
    private final TagRepository tagRepository;

    /**
     * Constructor for AdminController with dependency injection
     * @param userRepository Repository for user data operations
     * @param expenseRepository Repository for expense data operations
     * @param incomeRepository Repository for income data operations
     * @param budgetRepository Repository for budget data operations
     * @param tagRepository Repository for tag data operations
     */
    @Autowired
    public AdminController(UserRepository userRepository,
                           ExpenseRepository expenseRepository,
                           IncomeRepository incomeRepository,
                           BudgetRepository budgetRepository,
                           TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.budgetRepository = budgetRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Displays the admin dashboard with various platform metrics and data visualizations
     * @param model to be populated with dashboard data
     * @return String representing the view name for the admin dashboard
     */
    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {

        // --- 1. Key Platform Metrics ---
        // Calculate total number of users, budgets, and tags in the platform
        long totalUsers = userRepository.count();
        long totalBudgets = budgetRepository.count();
        long totalTags = tagRepository.count();

        // Retrieve all expenses and calculate total platform expenses
        List<Expens> allExpenses = expenseRepository.findAll();
        BigDecimal totalPlatformExpenses = allExpenses.stream()
                .map(Expens::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Retrieve all incomes and calculate total platform income
        List<Income> allIncomes = incomeRepository.findAll();
        BigDecimal totalPlatformIncome = allIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add platform metrics to the model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalBudgets", totalBudgets);
        model.addAttribute("totalTags", totalTags);
        model.addAttribute("totalPlatformExpenses", totalPlatformExpenses);
        model.addAttribute("totalPlatformIncome", totalPlatformIncome);

        // --- 2. Data for Tables ---
        // Add all users and recent expenses to the model for table display
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("recentExpenses", expenseRepository.findTop10ByOrderByExpenseDateDesc());

        // --- 3. Data for "Top Spending Users" Chart ---
        // Calculate total expenses per user
        Map<String, BigDecimal> expensesPerUser = allExpenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getUser().getUsername(),
                        Collectors.mapping(Expens::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        // Sort the map by value in descending order and get the top 5
        Map<String, BigDecimal> top5SpendingUsers = expensesPerUser.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        model.addAttribute("topSpenderLabels", new ArrayList<>(top5SpendingUsers.keySet()));
        model.addAttribute("topSpenderData", new ArrayList<>(top5SpendingUsers.values()));

        return "admin";
    }
}
package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.Budget;
import com.dami.expensetracker.models.Income;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; // 1. Import the a
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Optional;

@Controller // 2. Add the annotation here
public class HomeController {
    private final ExpenseRepository expenseRepository;
    private final TagRepository tagRepository;
    private final BudgetRepository budgetRepository;
    private final IncomeRepository incomeRepository;

    // Use constructor injection to get instances of your repositories
    public HomeController(ExpenseRepository expenseRepository,
                          TagRepository tagRepository,
                          BudgetRepository budgetRepository,
                          IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.tagRepository = tagRepository;
        this.budgetRepository = budgetRepository;
        this.incomeRepository = incomeRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        // 1. Get the total number of expenses
        long expenseCount = expenseRepository.count();

        // 2. Get the total number of tags
        long tagCount = tagRepository.count();

        // 3. Find the current ongoing budget using the new repository method
        Optional<Budget> currentBudgetOpt = budgetRepository.findCurrentBudget(LocalDate.now());

        // 4. Find the most recent income using the new repository method
        Optional<Income> recentIncomeOpt = incomeRepository.findTopByOrderByIncomeDateDesc();

        // Add all the fetched data to the model for the view to use
        model.addAttribute("expenseCount", expenseCount);
        model.addAttribute("tagCount", tagCount);
        model.addAttribute("currentBudget", currentBudgetOpt.orElse(null));
        model.addAttribute("recentIncome", recentIncomeOpt.orElse(null));

        return "index"; // Renders templates/index.html
    }



    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }
}

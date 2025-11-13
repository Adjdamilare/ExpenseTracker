package com.dami.expensetracker.controllers;

import com.dami.expensetracker.models.*;
import com.dami.expensetracker.repositories.BudgetRepository;
import com.dami.expensetracker.repositories.ExpenseRepository;
import com.dami.expensetracker.repositories.IncomeRepository;
import com.dami.expensetracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatisticsController {

    private final UserService userService;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final BudgetRepository budgetRepository;

    @Autowired
    public StatisticsController(UserService userService,
                                ExpenseRepository expenseRepository,
                                IncomeRepository incomeRepository,
                                BudgetRepository budgetRepository) {
        this.userService = userService;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * A helper class (DTO) to hold combined budget and spending information for the view.
     * This makes the Thymeleaf template much cleaner and easier to read.
     */
    public static class BudgetStatus {
        private String tagName;
        private BigDecimal budgetAmount;
        private BigDecimal spentAmount;
        private BigDecimal remainingAmount;
        private int percentageUsed;
        private String progressBarClass;

        //<editor-fold desc="Getters and Setters">
        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
        public BigDecimal getBudgetAmount() { return budgetAmount; }
        public void setBudgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; }
        public BigDecimal getSpentAmount() { return spentAmount; }
        public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }
        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
        public int getPercentageUsed() { return percentageUsed; }
        public void setPercentageUsed(int percentageUsed) { this.percentageUsed = percentageUsed; }
        public String getProgressBarClass() { return progressBarClass; }
        public void setProgressBarClass(String progressBarClass) { this.progressBarClass = progressBarClass; }
        //</editor-fold>
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model, Principal principal) {
        // 1. Get the current user
        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // --- STATISTIC 1: Key Metrics for the Current Month ---
        // Logic moved from IncomeService
        List<Income> monthlyIncomes = incomeRepository.findAllByUserAndIncomeDateBetween(currentUser, startOfMonth, endOfMonth);
        BigDecimal totalIncome = monthlyIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Logic moved from ExpenseService
        List<Expens> monthlyExpenses = expenseRepository.findAllByUserAndExpenseDateBetween(currentUser, startOfMonth, endOfMonth);
        BigDecimal totalExpenses = monthlyExpenses.stream()
                .map(Expens::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSavings = totalIncome.subtract(totalExpenses);

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("netSavings", netSavings);

        // --- STATISTIC 2: Expense Breakdown by Tag (Pie Chart) ---
        // This logic was already suitable for the controller
        Map<String, BigDecimal> expensesByTag = monthlyExpenses.stream()
                .flatMap(expense -> expense.getExpenseTags().stream())
                .collect(Collectors.groupingBy(
                        expenseTag -> expenseTag.getTag().getName(),
                        Collectors.mapping(expenseTag -> expenseTag.getExpense().getAmount(), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        model.addAttribute("tagLabels", new ArrayList<>(expensesByTag.keySet()));
        model.addAttribute("tagData", new ArrayList<>(expensesByTag.values()));

        // --- STATISTIC 3: Budget vs. Actual Spending (Bar Chart) ---
        List<Budget> userBudgets = budgetRepository.findByUser(currentUser);
        List<String> budgetLabels = new ArrayList<>();
        List<BigDecimal> budgetedAmounts = new ArrayList<>();
        List<BigDecimal> actualSpending = new ArrayList<>();

        for (Budget budget : userBudgets) {
            // Only include budgets active in the current month
            if (!budget.getStartDate().isAfter(endOfMonth) && !budget.getEndDate().isBefore(startOfMonth)) {
                budgetLabels.add(budget.getTag().getName());
                budgetedAmounts.add(budget.getAmount());

                // Logic to calculate spending for a specific tag within a date range
                List<Expens> budgetPeriodExpenses = expenseRepository.findAllByUserAndExpenseDateBetween(currentUser, budget.getStartDate(), budget.getEndDate());
                BigDecimal spent = budgetPeriodExpenses.stream()
                        .filter(expense -> expense.getExpenseTags().stream()
                                .anyMatch(expenseTag -> expenseTag.getTag().getId().equals(budget.getTag().getId())))
                        .map(Expens::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                actualSpending.add(spent);
            }
        }
        model.addAttribute("budgetLabels", budgetLabels);
        model.addAttribute("budgetedAmounts", budgetedAmounts);
        model.addAttribute("actualSpending", actualSpending);

        // --- STATISTIC 4: Monthly Expense Trend (Line Chart) ---
        Map<String, BigDecimal> monthlyTotals = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = today.minusMonths(i);
            String monthName = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            LocalDate start = month.withDayOfMonth(1);
            LocalDate end = month.withDayOfMonth(month.lengthOfMonth());

            // Logic to get total expenses for a given month
            List<Expens> loopMonthExpenses = expenseRepository.findAllByUserAndExpenseDateBetween(currentUser, start, end);
            BigDecimal total = loopMonthExpenses.stream()
                    .map(Expens::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyTotals.put(monthName, total);
        }

        model.addAttribute("monthLabels", new ArrayList<>(monthlyTotals.keySet()));
        model.addAttribute("monthData", new ArrayList<>(monthlyTotals.values()));

        return "statistics";
    }

    /**
     * NEW METHOD: Gathers data for the Expense vs. Budget tracking page.
     */
    @GetMapping("/expense-budget")
    public String showExpenseBudgets(Model model, Principal principal) {
        // 1. Get the current user
        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        // 2. Define the date range for the current month
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // 3. Fetch all user's budgets and this month's expenses (efficiently)
        List<Budget> allUserBudgets = budgetRepository.findByUser(currentUser);
        List<Expens> monthlyExpenses = expenseRepository.findAllByUserAndExpenseDateBetween(currentUser, startOfMonth, endOfMonth);

        // 4. Process the data to create a list of BudgetStatus objects
        List<BudgetStatus> budgetStatuses = new ArrayList<>();
        for (Budget budget : allUserBudgets) {
            // Only include budgets that are active during the current month
            if (!budget.getStartDate().isAfter(endOfMonth) && !budget.getEndDate().isBefore(startOfMonth)) {

                // Calculate total spent for this budget's specific tag
                BigDecimal spent = monthlyExpenses.stream()
                        .filter(expense -> expense.getExpenseTags().stream()
                                .anyMatch(et -> et.getTag().getId().equals(budget.getTag().getId())))
                        .map(Expens::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Calculate the percentage of the budget used
                int percentage = 0;
                if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    percentage = spent.multiply(new BigDecimal(100))
                            .divide(budget.getAmount(), 0, RoundingMode.HALF_UP)
                            .intValue();
                }

                // Determine the color of the progress bar based on spending
                String progressBarClass;
                if (percentage > 90) {
                    progressBarClass = "bg-danger"; // Over 90%
                } else if (percentage > 70) {
                    progressBarClass = "bg-warning"; // Over 70%
                } else {
                    progressBarClass = "bg-success"; // 70% or less
                }

                // Create and populate our DTO
                BudgetStatus status = new BudgetStatus();
                status.setTagName(budget.getTag().getName());
                status.setBudgetAmount(budget.getAmount());
                status.setSpentAmount(spent);
                status.setRemainingAmount(budget.getAmount().subtract(spent));
                status.setPercentageUsed(Math.min(percentage, 100)); // Cap at 100% for the visual
                status.setProgressBarClass(progressBarClass);

                budgetStatuses.add(status);
            }
        }

        // 5. Add the processed list and month name to the model
        model.addAttribute("budgetStatuses", budgetStatuses);
        model.addAttribute("currentMonthName", today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

        return "expense-budget";
    }

}
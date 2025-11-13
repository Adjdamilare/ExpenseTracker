package com.dami.expensetracker.services;

import com.dami.expensetracker.models.Budget;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }


    @Transactional
    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> findByUser(User currentUser) {
        return budgetRepository.findByUser(currentUser);
    }
}
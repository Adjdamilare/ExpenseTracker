package com.dami.expensetracker.services;

import com.dami.expensetracker.models.Expens;
import com.dami.expensetracker.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Saves a new or updated expense to the database.
     * @param expense The expense entity to save.
     */
    public void save(Expens expense) {
        expenseRepository.save(expense);
    }
}
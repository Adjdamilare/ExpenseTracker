package com.dami.expensetracker.services;

import com.dami.expensetracker.models.Income;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }


    @Transactional
    public Income save(Income income) {
        return incomeRepository.save(income);
    }

    public List<Income> findByUser(User currentUser) {
        return incomeRepository.findByUser(currentUser);
    }
}
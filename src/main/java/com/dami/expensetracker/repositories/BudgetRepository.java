package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Budget;
import com.dami.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
}
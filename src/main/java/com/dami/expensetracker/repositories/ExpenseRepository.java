package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Expens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expens, Integer> {
    // Spring Data JPA will automatically provide methods like save(), findById(), findAll(), etc.
    // You can add custom query methods here later if needed.
}
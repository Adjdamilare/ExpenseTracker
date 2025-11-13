package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Expens;
import com.dami.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expens, Integer> {
    // Spring Data JPA will automatically provide methods like save(), findById(), findAll(), etc.
    List<Expens> findAllByUserOrderByExpenseDateDesc(User user);
    // You can add custom query methods here later if needed.
}
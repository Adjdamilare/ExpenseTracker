package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Expens;
import com.dami.expensetracker.models.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expens, Integer> {
    // Spring Data JPA will automatically provide methods like save(), findById(), findAll(), etc.
    List<Expens> findAllByUserOrderByExpenseDateDesc(User user);

    List<Expens> findTop10ByOrderByExpenseDateDesc();

    List<Expens> findAllByUserAndExpenseDateBetween(User currentUser, @NotNull(message = "Start date is required.") LocalDate startDate, @NotNull(message = "End date is required.") LocalDate endDate);
    // You can add custom query methods here later if needed.
}
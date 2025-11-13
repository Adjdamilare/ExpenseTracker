package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Income;
import com.dami.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Integer> {
    // In IncomeRepository.java
    Optional<Income> findTopByOrderByIncomeDateDesc();

    List<Income> findByUser(User currentUser);
}
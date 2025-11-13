package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Budget;
import com.dami.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    /**
     * Finds the budget that is currently active based on the provided date.
     * @param currentDate The current date to check against the budget's start and end dates.
     * @return An Optional containing the active budget if found, otherwise empty.
     */
    @Query("SELECT b FROM Budget b WHERE :currentDate BETWEEN b.startDate AND b.endDate")
    Optional<Budget> findCurrentBudget(@Param("currentDate") LocalDate currentDate);


    List<Budget> findByUser(User currentUser);
}
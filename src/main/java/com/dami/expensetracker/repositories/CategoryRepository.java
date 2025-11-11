package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Category;
import com.dami.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    /**
     * Finds all categories created by a specific user AND all default categories
     * (where the user is null).
     */
    List<Category> findByUserOrUserIsNull(User user);
}
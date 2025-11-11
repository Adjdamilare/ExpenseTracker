package com.dami.expensetracker.services;

import com.dami.expensetracker.models.Category;
import com.dami.expensetracker.models.User;
import com.dami.expensetracker.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Gets a combined list of a user's custom categories and the default ones.
     */
    public List<Category> findCategoriesForUser(User user) {
        return categoryRepository.findByUserOrUserIsNull(user);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}
package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.Tag;
import com.dami.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    List<Tag> findByUserOrderByNameAsc(User user);

    // Add this method to check for duplicate tags for a specific user
    boolean existsByNameAndUser(String name, User user);
}
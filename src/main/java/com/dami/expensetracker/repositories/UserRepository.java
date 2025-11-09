package com.dami.expensetracker.repositories;

import com.dami.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);
    public User findByEmail(String email);
    List<User> findByEmailContainingIgnoreCase(String email);
    List<User> findByUsernameContainingIgnoreCase(String username);
}

package com.dami.expensetracker.services;

import com.dami.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional; // <-- 1. Import Optional

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.dami.expensetracker.models.User appUser = userRepository.findByEmail(email);

        if (appUser == null) {
            throw new UsernameNotFoundException("User with email '" + email + "' not found.");
        }

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPasswordHash())
                .roles("USER")
                .build();
    }

    // --- ADD THIS METHOD ---
    /**
     * Finds a user by their email (which is used as the username).
     * This method is used by controllers to get the full User entity.
     * @param email The email of the user to find.
     * @return An Optional containing the User if found, or an empty Optional otherwise.
     */
    public Optional<com.dami.expensetracker.models.User> findByUsername(String email) {
        // We use Optional.ofNullable to safely wrap the result.
        // If userRepository.findByEmail returns null, this will return an empty Optional.
        return Optional.ofNullable(userRepository.findByEmail(email));
    }
}
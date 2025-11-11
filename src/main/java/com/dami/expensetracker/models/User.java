package com.dami.expensetracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "expense_tracker")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "user")
    private Set<Budget> budgets = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Expens> expenses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Income> incomes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Tag> tags = new LinkedHashSet<>();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ")";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Compares users based on their ID. Assumes your ID field is named 'id'.
        // If your ID field is Long, this will still work.
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        // Uses the ID for the hash code, consistent with the equals method.
        return Objects.hash(id);
    }
}
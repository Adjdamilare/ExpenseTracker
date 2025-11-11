package com.dami.expensetracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "incomes", schema = "expense_tracker")
public class Income {
    @Id
    // --- IMPROVEMENT 1: Added @GeneratedValue ---
    // This tells JPA that the database is responsible for generating the ID.
    // Without this, you would get an error when trying to save a new income.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private com.dami.expensetracker.models.User user;

    @NotBlank(message = "Source cannot be empty.")
    @Size(max = 100, message = "Source cannot be longer than 100 characters.")
    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Income date is required.")
    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    // --- IMPROVEMENT 2: Added @PrePersist ---
    // This method automatically sets the 'createdAt' field to the current time
    // right before the new income record is saved to the database.
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
package com.dami.expensetracker.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "expenses", schema = "expense_tracker")
public class Expens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private com.dami.expensetracker.models.User user;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @ColumnDefault("0")
    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Lob
    @Column(name = "notes")
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "method_id", nullable = false)
    private PaymentMethod method;

    // --- REMOVED: The single tag relationship ---
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "tag_id")
    // private Tag tag;

    // --- ADDED: The many-to-many relationship via ExpenseTag ---
    @OneToMany(
            mappedBy = "expense",
            cascade = CascadeType.ALL, // This is key! It saves/updates/deletes ExpenseTags when an Expens is saved.
            orphanRemoval = true
    )
    private Set<ExpenseTag> expenseTags = new HashSet<>();

    // --- ADDED: Helper method for easily adding tags ---
    public void addTag(Tag tag) {
        ExpenseTag expenseTag = new ExpenseTag(this, tag);
        this.expenseTags.add(expenseTag);
    }
}
package com.dami.expensetracker.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "budgets", schema = "expense_tracker")
public class Budget {
    @Id
    @Column(name = "budget_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private com.dami.expensetracker.models.User user;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "month_num", nullable = false)
    private Byte monthNum;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

}
package com.dami.expensetracker.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "expense_tags", schema = "expense_tracker")
public class ExpenseTag {
    @EmbeddedId
    private ExpenseTagId id;

    @MapsId("expenseId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expens expense;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public ExpenseTag() {
        // --- FIX: Initialize the composite ID ---
        this.id = new ExpenseTagId();
    }

    public ExpenseTag(Expens expense, Tag tag) {
        // --- FIX: Initialize the composite ID object ---
        this.id = new ExpenseTagId();

        // Set the parent entities
        this.expense = expense;
        this.tag = tag;

        // Now, JPA can use @MapsId to correctly populate the fields of 'this.id'
        // from the IDs of 'this.expense' and 'this.tag' when the entity is persisted.
    }
}
package com.dami.expensetracker.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ExpenseTagId implements Serializable {
    private static final long serialVersionUID = 5473477268571393117L;
    @Column(name = "expense_id", nullable = false)
    private Integer expenseId;

    @Column(name = "tag_id", nullable = false)
    private Integer tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ExpenseTagId entity = (ExpenseTagId) o;
        return Objects.equals(this.tagId, entity.tagId) &&
                Objects.equals(this.expenseId, entity.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, expenseId);
    }

}
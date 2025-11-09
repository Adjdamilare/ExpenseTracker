package com.dami.expensetracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty; // Import
import jakarta.validation.constraints.Size;     // Import
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private Integer id;

    @NotEmpty(message = "Tag name cannot be empty.")
    @Size(max = 50, message = "Tag name cannot be longer than 50 characters.")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
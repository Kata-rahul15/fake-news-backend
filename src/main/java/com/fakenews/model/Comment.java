package com.fakenews.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private History history;

    @Column(nullable = false, length = 1000)
    private String text;

    private int karma = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isDeleted = false;

    // getters & setters
}

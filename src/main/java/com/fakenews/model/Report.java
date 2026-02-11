package com.fakenews.model;

import jakarta.persistence.*;
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String text;
    private String status;

    // ✅ REQUIRED by JPA
    public Report() {
    }

    // ✅ ADD THIS CONSTRUCTOR
    public Report(Long id, String text, String status) {
        this.id = id;
        this.text = text;
        this.status = status;
    }

    // getters & setters
}

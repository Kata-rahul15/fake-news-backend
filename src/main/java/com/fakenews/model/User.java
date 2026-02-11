package com.fakenews.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String fullName;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore   // 👈 ADD THIS
    private List<History> history;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // 📊 Stats
    private int checksPerformed = 0;
    private int karmaPoints = 0;
    private int correctPredictions = 0;
    private int totalVotes = 0;

    private boolean warningAcknowledged = false;

    //ADMIN CONTROLS
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    private int warnings = 0;

    @Column(name = "badge")
    private String badge;


    private String calculateBadge(int karmaPoints) {
        if (karmaPoints >= 300) return "Guardian";
        if (karmaPoints >= 150) return "Trusted";
        if (karmaPoints >= 50) return "Contributor";
        return "Newbie";
    }



}


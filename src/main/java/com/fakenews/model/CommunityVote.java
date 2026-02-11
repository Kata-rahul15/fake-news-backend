package com.fakenews.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(
        name = "community_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "history_id"})
)
public class CommunityVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType vote;


    @Column(name = "is_correct")
    private Boolean correct;

    // ✅ GETTERS & SETTERS

    public VoteType getVote() {
        return vote;
    }

    public void setVote(VoteType vote) {
        this.vote = vote;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setIsCorrect(Boolean correct) {
        this.correct = correct;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }


}

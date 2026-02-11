package com.fakenews.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "comment_id"}
        )
)
public class CommentVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private VoteDirection vote;

    private int karma = 0;// UP or DOWN

    // getters & setters
}

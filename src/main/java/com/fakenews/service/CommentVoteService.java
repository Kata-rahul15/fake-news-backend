package com.fakenews.service;

import com.fakenews.model.*;
import com.fakenews.repository.CommentRepository;
import com.fakenews.repository.CommentVoteRepository;
import com.fakenews.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentVoteService {

    private final CommentRepository commentRepo;
    private final CommentVoteRepository voteRepo;

    private final UserRepository userRepo;

    public CommentVoteService(CommentRepository commentRepo,
                              CommentVoteRepository voteRepo,
                              UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.voteRepo = voteRepo;
        this.userRepo = userRepo;
    }



    public void vote(User voter, Long commentId, VoteDirection direction) {

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow();

        // Prevent self-voting
        if (comment.getUser().getId().equals(voter.getId())) {
            throw new RuntimeException("You cannot vote on your own comment");
        }

        User commentOwner = comment.getUser(); // 👈 who gets impact

        CommentVote existing =
                voteRepo.findByUserIdAndCommentId(voter.getId(), commentId)
                        .orElse(null);

        if (existing != null) {
            if (existing.getVote() == direction) {
                return; // same vote → no change
            }

            // 🔁 undo old vote
            if (existing.getVote() == VoteDirection.UP) {
                comment.setKarma(comment.getKarma() - 1);
                commentOwner.setKarmaPoints(commentOwner.getKarmaPoints() - 1);
            } else {
                comment.setKarma(comment.getKarma() + 1);
                commentOwner.setKarmaPoints(commentOwner.getKarmaPoints() + 1);
            }

            existing.setVote(direction);
        } else {
            CommentVote vote = new CommentVote();
            vote.setUser(voter);
            vote.setComment(comment);
            vote.setVote(direction);
            voteRepo.save(vote);
        }

        // 🔥 apply new vote
        if (direction == VoteDirection.UP) {
            comment.setKarma(comment.getKarma() + 1);
            commentOwner.setKarmaPoints(commentOwner.getKarmaPoints() + 1);
        } else {
            comment.setKarma(comment.getKarma() - 1);
            commentOwner.setKarmaPoints(commentOwner.getKarmaPoints() - 1);
        }

        commentRepo.save(comment);
        userRepo.save(commentOwner);
    }

}

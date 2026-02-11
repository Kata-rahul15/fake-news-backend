package com.fakenews.repository;

import com.fakenews.model.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    Optional<CommentVote> findByUserIdAndCommentId(Long userId, Long commentId);
}

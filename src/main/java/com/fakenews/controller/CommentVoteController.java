package com.fakenews.controller;

import com.fakenews.model.User;
import com.fakenews.model.VoteDirection;
import com.fakenews.repository.UserRepository;
import com.fakenews.service.CommentVoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment-votes")
public class CommentVoteController {

    private final CommentVoteService commentVoteService;
    private final UserRepository userRepository;

    public CommentVoteController(CommentVoteService commentVoteService,
                                 UserRepository userRepository) {
        this.commentVoteService = commentVoteService;
        this.userRepository = userRepository;
    }

    // 👍 👎 COMMENT VOTE API
    @PostMapping("/{commentId}")
    public ResponseEntity<?> voteComment(
            @PathVariable Long commentId,
            @RequestParam VoteDirection direction,
            Authentication authentication
    ) {
        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        commentVoteService.vote(user, commentId, direction);
        return ResponseEntity.ok().build();
    }
}

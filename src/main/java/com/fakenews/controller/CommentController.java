package com.fakenews.controller;

import com.fakenews.model.Comment;
import com.fakenews.repository.CommentRepository;
import com.fakenews.repository.HistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.fakenews.model.*;
import com.fakenews.repository.UserRepository;
import com.fakenews.service.CommentVoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



    @RestController
    @RequestMapping("/api/comments")
    public class CommentController {

        private final CommentRepository commentRepo;
        private final HistoryRepository historyRepo;
        private final UserRepository userRepo;

        public CommentController(CommentRepository commentRepo,
                                 HistoryRepository historyRepo,
                                 UserRepository userRepo) {
            this.commentRepo = commentRepo;
            this.historyRepo = historyRepo;
            this.userRepo = userRepo;
        }

        // ✅ ADD COMMENT
        @PostMapping("/{historyId}")
        public ResponseEntity<?> addComment(
                @PathVariable Long historyId,
                @RequestBody String text,
                Authentication auth
        ) {
            text = URLDecoder.decode(text, StandardCharsets.UTF_8);
            User user = userRepo.findByEmail(auth.getName())
                    .orElseThrow();

            History history = historyRepo.findById(historyId)
                    .orElseThrow();

            Comment comment = new Comment();
            comment.setUser(user);
            comment.setHistory(history);
            comment.setText(text);
            comment.setKarma(0);

            commentRepo.save(comment);
            return ResponseEntity.ok().build();
        }

        // ✅ GET COMMENTS FOR A POST
        @GetMapping("/{historyId}")
        public List<Comment> getComments(@PathVariable Long historyId) {
            return commentRepo
                    .findByHistory_IdAndIsDeletedFalseOrderByKarmaDescCreatedAtAsc(historyId);
        }

    }



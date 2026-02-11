package com.fakenews.controller;

import com.fakenews.model.User;
import com.fakenews.model.VoteType;
import com.fakenews.service.VoteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.fakenews.model.User;
import com.fakenews.model.VoteType;
import com.fakenews.repository.UserRepository;
import com.fakenews.service.VoteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;
    private final UserRepository userRepository;

    public VoteController(VoteService voteService,
                          UserRepository userRepository) {
        this.voteService = voteService;
        this.userRepository = userRepository;
    }


    @PostMapping("/{historyId}")
    public ResponseEntity<?> vote(@PathVariable Long historyId,
                                  @RequestParam String vote) {

        VoteType voteType;
        try {
            voteType = VoteType.valueOf(vote.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid vote type: " + vote);
        }

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow();

        voteService.vote(user, historyId, voteType);
        return ResponseEntity.ok().build();
    }


}

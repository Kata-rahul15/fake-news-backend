package com.fakenews.controller;

import com.fakenews.dto.CommunityPostResponse;
import com.fakenews.model.User;
import com.fakenews.model.VoteType;
import com.fakenews.repository.CommunityVoteRepository;
import com.fakenews.repository.HistoryRepository;
import com.fakenews.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    private final HistoryRepository historyRepo;
    private final CommunityVoteRepository voteRepo;
    private final UserRepository userRepo;

    public CommunityController(HistoryRepository historyRepo,
                               CommunityVoteRepository voteRepo,
                               UserRepository userRepo) {
        this.historyRepo = historyRepo;
        this.voteRepo = voteRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/feed")
    public List<CommunityPostResponse> getFeed(Authentication authentication) {

        // 🔹 Resolve userId ONCE
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            userId = userRepo.findByEmail(authentication.getName())
                    .map(User::getId)
                    .orElse(null);
        }

        // ✅ Make it EFFECTIVELY FINAL
        final Long finalUserId = userId;

        return historyRepo.findAll()
                .stream()
                .map(h -> {

                    long real = voteRepo.countByHistoryIdAndVote(h.getId(), VoteType.REAL);
                    long fake = voteRepo.countByHistoryIdAndVote(h.getId(), VoteType.FAKE);
                    long unsure = voteRepo.countByHistoryIdAndVote(h.getId(), VoteType.UNVERIFIABLE);

                    String userVote = null;

                    if (finalUserId != null) {
                        userVote = voteRepo
                                .findByUserIdAndHistoryId(finalUserId, h.getId())
                                .map(v -> v.getVote().name().toLowerCase())
                                .orElse(null);
                    }

                    return new CommunityPostResponse(
                            h.getId(),
                            h.getText(),
                            h.getLabel(),
                            h.getConfidence() != null ? h.getConfidence() : 0.0,
                            real,
                            fake,
                            unsure,
                            userVote,
                            h.getCreatedAt()
                    );
                })
                .toList();
    }
}

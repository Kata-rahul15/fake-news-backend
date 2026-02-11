package com.fakenews.service;

import com.fakenews.model.*;
import com.fakenews.repository.CommunityVoteRepository;
import com.fakenews.repository.HistoryRepository;
import com.fakenews.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fakenews.model.User;

@Service
@Transactional
public class VoteService {

    private final CommunityVoteRepository voteRepo;
    private final UserRepository userRepo;
    private final HistoryRepository historyRepo;

    @Autowired
    private BadgeService badgeService;


    public VoteService(
            CommunityVoteRepository voteRepo,
            UserRepository userRepo,
            HistoryRepository historyRepo
    ) {
        this.voteRepo = voteRepo;
        this.userRepo = userRepo;
        this.historyRepo = historyRepo;
    }

    public void vote(User user, Long historyId, VoteType voteType) {

        if (voteRepo.existsByUserIdAndHistoryId(user.getId(), historyId)) {
            throw new IllegalStateException("User has already voted");

        }


        History history = historyRepo.findById(historyId)
                .orElseThrow();

        boolean isCorrect =
                voteType != VoteType.UNVERIFIABLE &&
                        voteType.name().equalsIgnoreCase(history.getLabel());

        CommunityVote vote = new CommunityVote();
        vote.setUser(user);
        vote.setHistory(history);
        vote.setVote(voteType);
        vote.setIsCorrect(isCorrect);
        voteRepo.save(vote);

        // 🔥 IMPACT LOGIC
        user.setTotalVotes(user.getTotalVotes() + 1);
        user.setKarmaPoints(user.getKarmaPoints() + 1); // +1 vote

        if (isCorrect) {
            user.setCorrectPredictions(user.getCorrectPredictions() + 1);
            user.setKarmaPoints(user.getKarmaPoints() + 1); // +1 correct
        }

        user.setBadge(badgeService.calculateBadge(user.getKarmaPoints()));
        System.out.println(
                "DEBUG → KARMA=" + user.getKarmaPoints() +
                        ", BADGE=" + user.getBadge()
        );

        userRepo.save(user);


    }
}

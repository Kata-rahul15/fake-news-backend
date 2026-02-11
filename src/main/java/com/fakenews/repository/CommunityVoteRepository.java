package com.fakenews.repository;

import com.fakenews.model.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fakenews.model.CommunityVote;
import java.util.Optional;

public interface CommunityVoteRepository
        extends JpaRepository<CommunityVote, Long> {


    Optional<CommunityVote> findByUserIdAndHistoryId(Long userId, Long historyId);
    boolean existsByUserIdAndHistoryId(Long userId, Long historyId);
    long countByHistoryIdAndVote(Long historyId, VoteType vote);

}

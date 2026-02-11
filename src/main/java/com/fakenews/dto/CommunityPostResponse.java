package com.fakenews.dto;

import java.time.LocalDateTime;

public record CommunityPostResponse(
        Long id,
        String text,
        String label,
        double confidence,
        long realVotes,
        long fakeVotes,
        long unsureVotes,
        String userVote,
        LocalDateTime createdAt
) {}

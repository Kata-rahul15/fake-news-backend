package com.fakenews.service;

import com.fakenews.dto.HomeStatsResponse;
import com.fakenews.repository.HistoryRepository;
import com.fakenews.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class HomeStatsService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    public HomeStatsService(HistoryRepository historyRepository,
                            UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    public HomeStatsResponse getHomeStats() {

        long totalChecks = historyRepository.count();
        long fakeNewsDetected = historyRepository.countByLabel("FAKE");
        long activeUsers = userRepository.count();

        int accuracyRate = totalChecks == 0
                ? 0
                : (int) (((double) (totalChecks - fakeNewsDetected) / totalChecks) * 100);

        return new HomeStatsResponse(
                totalChecks,
                activeUsers,
                accuracyRate,
                fakeNewsDetected
        );
    }
}

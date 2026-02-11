package com.fakenews.controller;

import com.fakenews.dto.DashboardResponse;
import com.fakenews.model.User;
import com.fakenews.repository.UserRepository;
import com.fakenews.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fakenews.model.User;
@RestController
@RequestMapping("/user")
public class UserDashboardController {

    private final UserRepository userRepository;
    public UserDashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Autowired
    private BadgeService badgeService;

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();



        User user = userRepository.findByEmail(email)
                .orElseThrow();

        int accuracy = user.getTotalVotes() > 0
                ? (int) Math.round(
                (user.getCorrectPredictions() * 100.0) / user.getTotalVotes()
        )
                : 0;

        // 🔥 ENSURE BADGE IS ALWAYS SET
        // 🔥 ALWAYS KEEP BADGE IN SYNC WITH KARMA
        String calculatedBadge = badgeService.calculateBadge(user.getKarmaPoints());

        if (!calculatedBadge.equals(user.getBadge())) {
            user.setBadge(calculatedBadge);
            userRepository.save(user);
        }



        return new DashboardResponse(
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt().toLocalDate(),
                user.getChecksPerformed(),
                accuracy,
                user.getKarmaPoints(),     // 🔥 SINGLE SOURCE
                user.getBadge()
        );
    }
}

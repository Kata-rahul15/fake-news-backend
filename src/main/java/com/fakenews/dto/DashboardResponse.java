package com.fakenews.dto;

import java.time.LocalDate;

public record DashboardResponse(
        String name,
        String email,
        LocalDate joinedAt,
        int checksPerformed,
        int accuracyRate,
        int impactScore,   // 🔥 maps to karmaPoints
        String badges
) {}

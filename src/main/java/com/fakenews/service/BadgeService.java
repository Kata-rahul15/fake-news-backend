package com.fakenews.service;


import org.springframework.stereotype.Service;

@Service
public class BadgeService {

    public String calculateBadge(int karmaPoints) {
        if (karmaPoints >= 300) return "Guardian";
        if (karmaPoints >= 150) return "Trusted";
        if (karmaPoints >= 50) return "Contributor";
        return "Newbie";
    }
}

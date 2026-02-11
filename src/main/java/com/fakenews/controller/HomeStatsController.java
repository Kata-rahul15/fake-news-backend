package com.fakenews.controller;
import com.fakenews.dto.HomeStatsResponse;
import com.fakenews.service.HomeStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class HomeStatsController {

    private final HomeStatsService homeStatsService;

    public HomeStatsController(HomeStatsService homeStatsService) {
        this.homeStatsService = homeStatsService;
    }

    @GetMapping("/home")
    public HomeStatsResponse getHomeStats() {
        return homeStatsService.getHomeStats();
    }
}

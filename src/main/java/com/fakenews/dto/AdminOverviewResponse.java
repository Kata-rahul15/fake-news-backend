package com.fakenews.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class AdminOverviewResponse {

    private long totalReports;
    private long fakeNews;
    private long realNews;
    private long unverifiable;
    private List<Map<String, Object>> weeklyData;

    public AdminOverviewResponse(
            long totalReports,
            long fakeNews,
            long realNews,
            long unverifiable,
            List<Map<String, Object>> weeklyData
    ) {
        this.totalReports = totalReports;
        this.fakeNews = fakeNews;
        this.realNews = realNews;
        this.unverifiable = unverifiable;
        this.weeklyData = weeklyData;
    }

}

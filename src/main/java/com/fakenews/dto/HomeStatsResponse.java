package com.fakenews.dto;

public class HomeStatsResponse {

    private long newsChecked;
    private long activeUsers;
    private int accuracyRate;
    private long fakeNewsDetected;

    public HomeStatsResponse(long newsChecked, long activeUsers, int accuracyRate, long fakeNewsDetected) {
        this.newsChecked = newsChecked;
        this.activeUsers = activeUsers;
        this.accuracyRate = accuracyRate;
        this.fakeNewsDetected = fakeNewsDetected;
    }

    public long getNewsChecked() {
        return newsChecked;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public int getAccuracyRate() {
        return accuracyRate;
    }

    public long getFakeNewsDetected() {
        return fakeNewsDetected;
    }
}


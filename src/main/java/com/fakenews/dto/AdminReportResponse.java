package com.fakenews.dto;

public class AdminReportResponse {

    private Long id;
    private String text;
    private String status;

    public AdminReportResponse(Long id, String text, String status) {
        this.id = id;
        this.text = text;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getStatus() {
        return status;
    }
}


package com.fakenews.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String role;
    private String status;
    private Long userId;

    // ✅ REQUIRED: no-arg constructor
    public AuthResponse() {
    }

    // ✅ REGISTER + OLD LOGIN
    public AuthResponse(String token, String role, String status) {
        this.token = token;
        this.role = role;
        this.status = status;
    }

    // ✅ NEW LOGIN (with userId)
    public AuthResponse(String token, String role, String status, Long userId) {
        this.token = token;
        this.role = role;
        this.status = status;
        this.userId = userId;
    }

    // ✅ ERROR / SUSPENDED RESPONSE
    public AuthResponse(String token, String message) {
        this.token = token;
        this.status = message;
    }

    // getters & setters
}

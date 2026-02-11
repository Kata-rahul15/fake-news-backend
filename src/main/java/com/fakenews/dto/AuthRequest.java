package com.fakenews.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
    private String confirmPassword;   // only used in register
    private String fullName;

}

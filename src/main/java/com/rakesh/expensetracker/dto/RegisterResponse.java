package com.rakesh.expensetracker.dto;

public class RegisterResponse {
    private Long userId;
    private String email;

    public RegisterResponse(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}

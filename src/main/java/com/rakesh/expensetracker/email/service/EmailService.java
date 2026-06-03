package com.rakesh.expensetracker.email.service;

public interface EmailService {

    void sendEmail(
            String to,
            String subject,
            String body
    );
}
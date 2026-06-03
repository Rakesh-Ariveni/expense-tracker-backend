package com.rakesh.expensetracker.email.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rakesh.expensetracker.email.service.EmailService;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(
            EmailService emailService
    ) {
        this.emailService = emailService;
    }

    @GetMapping("/email/test")
    public String sendTestMail() {

        emailService.sendEmail(
                "YOUR_EMAIL@gmail.com",
                "Expense Tracker Test",
                "Email service working successfully."
        );

        return "Mail Sent";
    }
}
package com.friendfinder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "Verify your Friend Finder account";
        String text = "Your OTP for verification is: " + otp;
        sendEmail(to, subject, text);
    }

    public void sendNotification(String to, String subject, String text) {
        sendEmail(to, subject, text);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@friendfinder.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            // Mock behavior if mail server is not configured
            logger.error("Failed to send email. Mail Server may not be configured. Mocking email sending.");
            logger.info("MOCK EMAIL TO: {}, SUBJECT: {}, TEXT: {}", to, subject, text);
        }
    }
}

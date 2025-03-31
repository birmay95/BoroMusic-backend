package com.example.music_platform.service;

import com.example.music_platform.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Data
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String token) {
        String subject = "Confirm Your Email for BoroMusic";

        String message = String.format(
                "Hello, %s!\n\n" +
                        "Thank you for registering at BoroMusic.\n\n" +
                        "To complete your registration and verify your email address, please use the verification code below:\n\n" +
                        "Verification Code: %s\n\n" +
                        "If you did not create an account, please ignore this email.\n\n" +
                        "Best regards,\nThe BoroMusic Team",
                user.getUsername(), token
        );

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

}


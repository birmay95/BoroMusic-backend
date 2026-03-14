package com.baravenski.musicplatform.core.email.service;

import com.baravenski.musicplatform.core.email.client.EmailClient;
import com.baravenski.musicplatform.user.model.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

@Service
@NullMarked
@RequiredArgsConstructor
public class EmailService {

    private final EmailClient emailClient;

    public void sendVerificationEmail(User user, String token) {
        String subject = "Confirm Your Email for BoroMusic";
        String message = """
                Hello, %s!
                
                Thank you for registering at BoroMusic.
                
                To complete your registration and verify your email address, please use the verification code below:
                
                Verification Code: %s
                
                If you did not create an account, please ignore this email.
                
                Best regards,
                The BoroMusic Team
                """.formatted(user.getUsername(), token);

        emailClient.sendEmail(user.getEmail(), subject, message);
    }
}
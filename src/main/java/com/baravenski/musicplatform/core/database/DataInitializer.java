package com.baravenski.musicplatform.core.database;

import com.baravenski.musicplatform.core.security.enums.UserRoles;
import com.baravenski.musicplatform.user.model.User;
import com.baravenski.musicplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.username}")
    private String adminUsername;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            String password = passwordEncoder.encode(adminPassword);

            User user = User.builder()
                    .email(adminEmail)
                    .username(adminUsername)
                    .password(password)
                    .role(UserRoles.ADMIN)
                    .emailVerified(true)
                    .build();
            userRepository.save(user);

            log.info("User '{}' added to the database", adminUsername);
        }
    }
}


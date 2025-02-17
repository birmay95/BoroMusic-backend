package com.example.music_platform.config;

import com.example.music_platform.model.User;
import com.example.music_platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("bor@gmail.com").isEmpty()) {
            // Хеширование пароля перед сохранением
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String password = encoder.encode("12345678");

            // Сохраняем пользователя
            User user = new User(null, "bor@gmail.com", "mishail", password, "ADMIN", true);
            userRepository.save(user);

            System.out.println("Пользователь добавлен в базу данных");
        }
    }
}

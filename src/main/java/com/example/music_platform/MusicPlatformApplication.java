package com.example.music_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MusicPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicPlatformApplication.class, args);
	}

}

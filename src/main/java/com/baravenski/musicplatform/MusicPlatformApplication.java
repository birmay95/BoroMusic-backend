package com.baravenski.musicplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration.class,
        io.awspring.cloud.autoconfigure.core.AwsAutoConfiguration.class
})
public class MusicPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicPlatformApplication.class, args);
    }

}

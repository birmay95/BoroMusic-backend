package com.example.music_platform.dto;

import com.example.music_platform.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackDTO {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private String fileName;  // Имя файла в хранилище
    private String contentType; // Тип контента, например "audio/mpeg"
    private Long fileSize;     // Размер файла в байтах
    private Long duration;
    private Set<Genre> genres;
}

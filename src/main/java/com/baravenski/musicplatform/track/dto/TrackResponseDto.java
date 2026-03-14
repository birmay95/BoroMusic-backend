package com.baravenski.musicplatform.track.dto;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

@NullMarked
public record TrackResponseDto(
        UUID id,
        String title,
        String artist,
        String album,
        String fileName,
        String contentType,
        Long fileSize,
        Long duration,
        List<String> genres
) {
}
package com.baravenski.musicplatform.playlist.dto;

import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public record PlaylistRequestDto(
        String name,
        String description,
        UUID userId
) {
}

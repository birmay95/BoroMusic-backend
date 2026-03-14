package com.baravenski.musicplatform.playlist.dto;

import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public record PlaylistResponseDto(
        UUID id,
        String name,
        String description
) {
}
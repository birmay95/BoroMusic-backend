package com.baravenski.musicplatform.user.dto;

import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public record UserResponseDto(
        UUID id,
        String email,
        String username,
        String role,
        boolean emailVerified
) {
}

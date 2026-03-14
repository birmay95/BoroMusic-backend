package com.baravenski.musicplatform.user.dto;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String email,
        String username,
        String role,
        boolean emailVerified
) {
}
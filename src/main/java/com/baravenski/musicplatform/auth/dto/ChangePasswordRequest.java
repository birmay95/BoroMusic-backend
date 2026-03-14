package com.baravenski.musicplatform.auth.dto;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}


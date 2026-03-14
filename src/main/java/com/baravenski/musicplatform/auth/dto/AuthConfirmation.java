package com.baravenski.musicplatform.auth.dto;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record AuthConfirmation(
        String token
) {
}

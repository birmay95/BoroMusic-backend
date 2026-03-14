package com.baravenski.musicplatform.exception.dto;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ExceptionMessageDto(
        int statusCode,
        String timestamp,
        String message,
        String description
) {
}

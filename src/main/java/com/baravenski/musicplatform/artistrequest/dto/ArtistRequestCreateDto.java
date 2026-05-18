package com.baravenski.musicplatform.artistrequest.dto;

import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public record ArtistRequestCreateDto(
        UUID userId
) {
}

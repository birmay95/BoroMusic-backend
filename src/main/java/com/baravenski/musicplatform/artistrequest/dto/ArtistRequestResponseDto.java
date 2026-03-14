package com.baravenski.musicplatform.artistrequest.dto;

import com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;
import java.util.UUID;

@NullMarked
public record ArtistRequestResponseDto(
        UUID id,
        LocalDateTime createdAt,
        ArtistRequestStatus status

) {
}

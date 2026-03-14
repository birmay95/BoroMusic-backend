package com.baravenski.musicplatform.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public record RecommendationDto(
        @JsonProperty("track_id") UUID trackId,
        String valence,
        String arousal
) {
}
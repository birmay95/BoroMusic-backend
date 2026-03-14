package com.baravenski.musicplatform.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record RecommendationResponseWrapper(
        @JsonProperty("recommendations") List<RecommendationDto> recommendations
) {
}
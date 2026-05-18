package com.baravenski.musicplatform.recommendation.dto;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record MlRecommendRequest(
        String track_id,
        List<String> excluded_ids
) {
}

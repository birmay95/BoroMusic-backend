package com.baravenski.musicplatform.recommendation.dto;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record MlPersonalRecommendRequest(
        List<String> liked_track_ids,
        List<String> excluded_ids
) {}
package com.baravenski.musicplatform.recommendation.dto;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

@NullMarked
public record ExcludedTracksRequest(
        List<UUID> excludedIds
) {
}

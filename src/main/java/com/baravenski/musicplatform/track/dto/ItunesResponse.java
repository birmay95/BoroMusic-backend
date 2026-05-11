package com.baravenski.musicplatform.track.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@JsonIgnoreProperties(ignoreUnknown = true)
public record ItunesResponse(
        @Nullable Integer resultCount,
        List<ItunesTrack> results
) {
}

package com.baravenski.musicplatform.track.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@JsonIgnoreProperties(ignoreUnknown = true)
public record ItunesTrack(
        String artistName,
        String trackName,
        @Nullable String collectionName,
        @Nullable String primaryGenreName,
        String artworkUrl100
) {
}

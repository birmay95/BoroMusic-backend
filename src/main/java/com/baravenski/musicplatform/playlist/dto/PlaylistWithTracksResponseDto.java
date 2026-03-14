package com.baravenski.musicplatform.playlist.dto;

import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

@NullMarked
public record PlaylistWithTracksResponseDto(
        UUID id,
        String name,
        String description,
        List<TrackResponseDto> tracks
) {
}
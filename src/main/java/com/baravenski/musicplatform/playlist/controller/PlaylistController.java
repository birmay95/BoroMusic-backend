package com.baravenski.musicplatform.playlist.controller;

import com.baravenski.musicplatform.core.pagination.PageResponseDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistRequestDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistResponseDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistWithTracksResponseDto;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.playlist.service.PlaylistService;
import com.baravenski.musicplatform.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final TrackService trackService;

    @PostMapping
    @ResponseStatus(OK)
    public PlaylistResponseDto createPlaylist(@RequestBody PlaylistRequestDto playlistRequestDto) {
        return playlistService.createPlaylist(playlistRequestDto);
    }

    @ResponseStatus(OK)
    @PostMapping("/{id}/tracks/{trackId}")
    public PlaylistWithTracksResponseDto addTrackToPlaylist(@PathVariable UUID id, @PathVariable UUID trackId) {
        return playlistService.addTrackToPlaylist(id, trackId);
    }

    @ResponseStatus(OK)
    @DeleteMapping("/{id}/tracks/{trackId}")
    public PlaylistWithTracksResponseDto removeTrackToPlaylist(@PathVariable UUID id, @PathVariable UUID trackId) {
        return playlistService.removeTrackFromPlaylist(id, trackId);
    }

    @ResponseStatus(OK)
    @GetMapping("/{playlistId}/tracks")
    public List<TrackResponseDto> getTracksInPlaylist(@PathVariable UUID playlistId) {
        return trackService.getTracksByPlaylistId(playlistId);
    }

    @ResponseStatus(OK)
    @GetMapping("/{id}")
    public PlaylistWithTracksResponseDto getPlaylistWithTracksById(@PathVariable UUID id) {
        return playlistService.getPlaylistWithTracks(id);
    }

    @GetMapping
    @ResponseStatus(OK)
    public PageResponseDto<PlaylistWithTracksResponseDto> getPlaylists(@RequestParam(defaultValue = "0") int page) {
        return playlistService.getPlaylists(page);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deletePlaylist(@PathVariable UUID id) {
        playlistService.deletePlaylist(id);
    }

    @ResponseStatus(OK)
    @GetMapping("users/{userId}/playlists")
    public List<PlaylistResponseDto> getPlaylistsByUserId(@PathVariable UUID userId) {
        return playlistService.getPlaylistsByUserId(userId);
    }
}

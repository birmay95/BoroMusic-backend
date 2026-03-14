package com.baravenski.musicplatform.playlist.controller;

import com.baravenski.musicplatform.playlist.dto.PlaylistRequestDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistResponseDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistWithTracksResponseDto;
import com.baravenski.musicplatform.playlist.model.Playlist;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.playlist.service.PlaylistService;
import com.baravenski.musicplatform.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/playlists")
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
    public List<PlaylistWithTracksResponseDto> getPlaylists() {
        return playlistService.getPlaylists();
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

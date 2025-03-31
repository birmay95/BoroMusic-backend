package com.example.music_platform.controller;

import com.example.music_platform.model.Playlist;
import com.example.music_platform.model.Track;
import com.example.music_platform.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestParam String name, @RequestParam String description, @RequestParam Long userId) {
        Playlist playlist = playlistService.createPlaylist(name, description, userId);
        return ResponseEntity.ok(playlist);
    }

    @PostMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<Playlist> addTrackToPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        return playlistService.addTrackToPlaylist(playlistId, trackId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<Playlist> removeTrackToPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        return playlistService.removeTrackFromPlaylist(playlistId, trackId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<Set<Track>> getTracksInPlaylist(@PathVariable Long playlistId) {
        Set<Track> tracks = playlistService.getTracksInPlaylist(playlistId);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.getPlaylist(playlistId);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping
    public ResponseEntity<List<Playlist>> getPlaylists() {
        List<Playlist> playlists = playlistService.getPlaylists();
        return ResponseEntity.ok(playlists);
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long playlistId) {
        playlistService.deletePlaylist(playlistId);
        return ResponseEntity.noContent().build();
    }
}

package com.baravenski.musicplatform.playlist.service;

import com.baravenski.musicplatform.exception.impl.PlaylistNotFoundException;
import com.baravenski.musicplatform.playlist.dto.PlaylistRequestDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistResponseDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistWithTracksResponseDto;
import com.baravenski.musicplatform.playlist.dto.mapper.PlaylistMapper;
import com.baravenski.musicplatform.playlist.repository.PlaylistRepository;
import com.baravenski.musicplatform.track.service.TrackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;
    private final TrackService trackService;

    @CachePut(value = "playlist", key = "#result.id")
    @CacheEvict(value = "playlists", allEntries = true)
    public PlaylistResponseDto createPlaylist(PlaylistRequestDto playlistRequestDto) {
        var playlistToSave = playlistMapper.toEntity(playlistRequestDto);
        var playlistSaved = playlistRepository.save(playlistToSave);
        return playlistMapper.toDto(playlistSaved);
    }

    @Transactional
    @CacheEvict(value = "playlists", allEntries = true)
    public PlaylistWithTracksResponseDto addTrackToPlaylist(UUID id, UUID trackId) {
        var playlist = playlistRepository.findPlaylistWithTracksById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(id));
        var track = trackService.findTrackById(trackId);

        playlist.getTracks().add(track);
        playlist = playlistRepository.save(playlist);
        trackService.fetchGenresForTracks(playlist.getTracks());
        return playlistMapper.toDtoWithTracks(playlist);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "playlists", allEntries = true),
            @CacheEvict(value = "playlist", key = "#id")
    })
    public PlaylistWithTracksResponseDto removeTrackFromPlaylist(UUID id, UUID trackId) {
        var playlist = playlistRepository.findPlaylistWithTracksById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(id));
        var track = trackService.findTrackById(trackId);

        if (playlist.getTracks().contains(track)) {
            playlist.getTracks().remove(track);
            playlist = playlistRepository.save(playlist);
        }
        trackService.fetchGenresForTracks(playlist.getTracks());
        return playlistMapper.toDtoWithTracks(playlist);
    }

    @Transactional
    @Cacheable(value = "playlist", key = "#id")
    public PlaylistWithTracksResponseDto getPlaylistWithTracks(UUID id) {
        var playlist = playlistRepository.findPlaylistWithTracksById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(id));
        trackService.fetchGenresForTracks(playlist.getTracks());
        return playlistMapper.toDtoWithTracks(playlist);
    }

    @Transactional
    @Cacheable("playlists")
    public List<PlaylistWithTracksResponseDto> getPlaylists() {
        var playlists = playlistRepository.findAllWithTracks();
        var allTracks = playlists.stream()
                .flatMap(p -> p.getTracks().stream())
                .distinct()
                .toList();
        trackService.fetchGenresForTracks(allTracks);
        return playlistMapper.toDtoListWithTracks(playlists);
    }

    @Caching(evict = {
            @CacheEvict(value = "playlists", allEntries = true),
            @CacheEvict(value = "playlist", key = "#id")
    })
    public void deletePlaylist(UUID id) {
        playlistRepository.deleteById(id);
    }

    public List<PlaylistResponseDto> getPlaylistsByUserId(UUID userId) {
        var playlists = playlistRepository.findPlaylistsByUserId(userId);
        return playlistMapper.toDtoList(playlists);
    }
}

package com.example.music_platform.service;

import com.example.music_platform.exception.PlaylistNotFoundException;
import com.example.music_platform.exception.UserNotFoundException;
import com.example.music_platform.model.Playlist;
import com.example.music_platform.model.Track;
import com.example.music_platform.model.User;
import com.example.music_platform.repository.PlaylistRepository;
import com.example.music_platform.repository.TrackRepository;
import com.example.music_platform.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    @CachePut(value = "playlist", key = "#result.id")
    @CacheEvict(value = "playlists", allEntries = true)
    public Playlist createPlaylist(String name, String description, Long userId) {
        User user = userRepository.findByIdWithPlaylists(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setOwner(user);
        return playlistRepository.save(playlist);
    }

    @CacheEvict(value = "playlists", allEntries = true)
    public Optional<Playlist> addTrackToPlaylist(Long playlistId, Long trackId) {
        Optional<Playlist> playlistOpt = playlistRepository.findPlaylistWithTracksAndGenresById(playlistId);
        Optional<Track> trackOpt = trackRepository.findTrackWithGenresById(trackId);

        if (playlistOpt.isPresent() && trackOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();

            playlist.getTracks().add(trackOpt.get());

            playlistRepository.save(playlist);
            return Optional.of(playlist);
        }
        return Optional.empty();
    }

    @Caching(evict = {
            @CacheEvict(value = "playlists", allEntries = true),
            @CacheEvict(value = "playlist", key = "#playlistId")
    })
    public Optional<Playlist> removeTrackFromPlaylist(Long playlistId, Long trackId) {
        Optional<Playlist> playlistOpt = playlistRepository.findPlaylistWithTracksAndGenresById(playlistId);
        Optional<Track> trackOpt = trackRepository.findTrackWithGenresById(trackId);

        if (playlistOpt.isPresent() && trackOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Track track = trackOpt.get();

            if (playlist.getTracks().contains(track)) {
                playlist.getTracks().remove(track);
                playlistRepository.save(playlist);
                return Optional.of(playlist);
            }
        }
        return Optional.empty();
    }

    public Set<Track> getTracksInPlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findPlaylistWithTracksAndGenresById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));
        return playlist.getTracks();
    }

    @Cacheable(value = "playlist", key = "#playlistId")
    public Playlist getPlaylist(Long playlistId) {
        return playlistRepository.findPlaylistWithTracksAndGenresById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));
    }

    @Cacheable("playlists")
    public List<Playlist> getPlaylists() {
        return playlistRepository.findAllWithTracksAndGenresById();
    }

    @Caching(evict = {
            @CacheEvict(value = "playlists", allEntries = true),
            @CacheEvict(value = "playlist", key = "#playlistId")
    })
    public void deletePlaylist(Long playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);

        if (playlistOpt.isPresent()) {
//            Playlist playlist = playlistOpt.get();
//            playlist.getTracks().forEach(track -> track.getPlaylists().remove(playlist));
            playlistRepository.deleteById(playlistId);
        } else {
            throw new PlaylistNotFoundException("Playlist not found");
        }
    }
}

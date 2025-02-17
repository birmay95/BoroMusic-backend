package com.example.music_platform.service;

import com.example.music_platform.model.Playlist;
import com.example.music_platform.model.Track;
import com.example.music_platform.repository.PlaylistRepository;
import com.example.music_platform.repository.TrackRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@AllArgsConstructor
@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    // Создание нового плейлиста
    public Playlist createPlaylist(String name, String description) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setDescription(description);
        return playlistRepository.save(playlist);
    }

    // Добавление трека в плейлист
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

    public Optional<Playlist> removeTrackFromPlaylist(Long playlistId, Long trackId) {
        // Ищем плейлист с подгруженными треками и жанрами
        Optional<Playlist> playlistOpt = playlistRepository.findPlaylistWithTracksAndGenresById(playlistId);
        Optional<Track> trackOpt = trackRepository.findTrackWithGenresById(trackId);

        if (playlistOpt.isPresent() && trackOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Track track = trackOpt.get();

            // Удаляем трек из коллекции треков плейлиста
            if (playlist.getTracks().contains(track)) {
                playlist.getTracks().remove(track);
                playlistRepository.save(playlist);  // Сохраняем изменения в базе данных
                return Optional.of(playlist);
            }
        }
        return Optional.empty();  // Возвращаем пустой Optional, если трек или плейлист не найдены
    }


    // Получение всех треков из плейлиста
    public Set<Track> getTracksInPlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findPlaylistWithTracksAndGenresById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return playlist.getTracks();
    }

    public Playlist getPlaylist(Long playlistId) {
        return playlistRepository.findPlaylistWithTracksAndGenresById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
    }

    public List<Playlist> getPlaylists() {
        return playlistRepository.findAllWithTracksAndGenresById();
    }

    public void deletePlaylist(Long playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);

        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            playlist.getTracks().forEach(track -> track.getPlaylists().remove(playlist)); // Удаляем связи с треками
            playlistRepository.deleteById(playlistId);  // Удаляем плейлист
        } else {
            throw new RuntimeException("Playlist not found");
        }
    }
}

package com.baravenski.musicplatform.playlist.repository;

import com.baravenski.musicplatform.playlist.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.tracks WHERE p.id = :playlistId")
    Optional<Playlist> findPlaylistWithTracksById(@Param("playlistId") UUID id);

    @Query("SELECT DISTINCT p FROM Playlist p " +
            "LEFT JOIN FETCH p.tracks t " +
            "LEFT JOIN FETCH t.genres " +
            "WHERE p.id = :playlistId")
    Optional<Playlist> findPlaylistWithTracksAndGenresById(@Param("playlistId") UUID id);

    @Query("SELECT DISTINCT p FROM Playlist p " +
            "LEFT JOIN FETCH p.tracks t " +
            "LEFT JOIN FETCH t.genres ")
    List<Playlist> findAllWithTracksAndGenres();

    @Query("""
                    SELECT playlists FROM User user
                    JOIN user.playlists playlists
                    WHERE user.id = :userId
            """)
    List<Playlist> findPlaylistsByUserId(@Param("userId") UUID userId);
}

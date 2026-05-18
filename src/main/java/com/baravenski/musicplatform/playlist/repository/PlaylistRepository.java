package com.baravenski.musicplatform.playlist.repository;

import com.baravenski.musicplatform.playlist.model.Playlist;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.tracks WHERE p.id = :playlistId")
    Optional<Playlist> findPlaylistWithTracksById(@Param("playlistId") UUID id);

    @Query("""
                    SELECT playlists FROM User user
                    JOIN user.playlists playlists
                    WHERE user.id = :userId
            """)
    List<Playlist> findPlaylistsByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT p FROM Playlist p", countQuery = "SELECT count(p) FROM Playlist p")
    Page<Playlist> findAllPlaylists(Pageable pageable);

    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.tracks WHERE p IN :playlists")
    List<Playlist> fetchTracksForPlaylists(@Param("playlists") List<Playlist> playlists);
}

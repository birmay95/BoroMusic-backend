package com.example.music_platform.repository;

import com.example.music_platform.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

//    @Query("SELECT DISTINCT c FROM Playlist c "
//            + "LEFT JOIN FETCH c.tracks WHERE c.id = :id")
//    Optional<Playlist> findPlaylistWithTracksById(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.tracks WHERE p.id = :playlistId")
    Optional<Playlist> findPlaylistWithTracksById(@Param("playlistId") Long playlistId);

    @Query("SELECT DISTINCT p FROM Playlist p " +
            "LEFT JOIN FETCH p.tracks t " +
            "LEFT JOIN FETCH t.genres " +
            "WHERE p.id = :playlistId")
    Optional<Playlist> findPlaylistWithTracksAndGenresById(@Param("playlistId") Long playlistId);

    @Query("SELECT DISTINCT p FROM Playlist p " +
            "LEFT JOIN FETCH p.tracks t " +
            "LEFT JOIN FETCH t.genres ")
    List<Playlist> findAllWithTracksAndGenresById();


}

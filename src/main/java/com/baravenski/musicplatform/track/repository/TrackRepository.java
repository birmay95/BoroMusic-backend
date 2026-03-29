package com.baravenski.musicplatform.track.repository;

import com.baravenski.musicplatform.track.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, UUID> {

    Optional<Track> findByFileName(String fileName);

    @Query("SELECT DISTINCT t FROM Track t LEFT JOIN FETCH t.genres WHERE t.id = :trackId")
    Optional<Track> findTrackWithGenresById(@Param("trackId") UUID trackId);

    @Query("SELECT DISTINCT t FROM Track t LEFT JOIN FETCH t.genres")
    List<Track> findAllWithGenres();

    @Modifying
    @Query(value = "DELETE FROM user_favourites WHERE track_id = CAST(:id AS uuid)", nativeQuery = true)
    void deleteTrackFromFavourites(@Param("id") UUID id);

    @Modifying
    @Query(value = "DELETE FROM playlist_tracks WHERE track_id = CAST(:id AS uuid)", nativeQuery = true)
    void deleteTrackFromPlaylists(@Param("id") UUID id);

    @Modifying
    @Query(value = "DELETE FROM track_genres WHERE track_id = CAST(:id AS uuid)", nativeQuery = true)
    void deleteTrackGenres(@Param("id") UUID id);

    @Query("""
                    SELECT tracks FROM Playlist playlist
                    JOIN playlist.tracks tracks
                    WHERE playlist.id = :playlistId
            """)
    List<Track> findTracksByPlaylistId(@Param("playlistId") UUID playlistId);

    @Query("""
                    SELECT tracks FROM User user
                    JOIN user.favourites tracks
                    LEFT JOIN FETCH tracks.genres
                    WHERE user.id = :userId
            """)
    List<Track> findTracksByUserId(@Param("userId") UUID userId);

    @Query("SELECT DISTINCT t FROM Track t LEFT JOIN FETCH t.genres WHERE t IN :tracks")
    List<Track> fetchGenresForTracks(@Param("tracks") List<Track> tracks);
}
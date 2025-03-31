package com.example.music_platform.repository;

import com.example.music_platform.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    Optional<Track> findByFileName(String fileName);

    @Query("SELECT DISTINCT t FROM Track t LEFT JOIN FETCH t.genres WHERE t.id = :trackId")
    Optional<Track> findTrackWithGenresById(@Param("trackId") Long trackId);

    @Query("SELECT DISTINCT t FROM Track t LEFT JOIN FETCH t.genres")
    List<Track> findAllWithGenres();
}
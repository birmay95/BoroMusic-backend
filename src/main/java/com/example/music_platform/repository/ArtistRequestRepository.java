package com.example.music_platform.repository;

import com.example.music_platform.model.ArtistRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRequestRepository extends JpaRepository<ArtistRequest, Long> {

    Optional<ArtistRequest> findByUserId(Long userId);
}


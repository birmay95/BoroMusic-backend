package com.baravenski.musicplatform.artistrequest.repository;

import com.baravenski.musicplatform.artistrequest.model.ArtistRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRequestRepository extends JpaRepository<ArtistRequest, UUID> {

    Optional<ArtistRequest> findByUserId(UUID userId);

    @Query("SELECT artistRequest FROM ArtistRequest artistRequest " +
            "LEFT JOIN FETCH artistRequest.user user " +
            "WHERE artistRequest.id = :artistRequestId")
    Optional<ArtistRequest> findArtistRequestWithUserById(UUID artistRequestId);

    @Query("SELECT artistRequest FROM ArtistRequest artistRequest " +
            "LEFT JOIN FETCH artistRequest.user user")
    List<ArtistRequest> findArtistRequestsWithUser();

    void deleteByUserId(UUID userId);
}


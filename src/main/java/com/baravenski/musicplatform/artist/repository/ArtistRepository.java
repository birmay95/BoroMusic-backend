package com.baravenski.musicplatform.artist.repository;

import com.baravenski.musicplatform.artist.model.Artist;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@NullMarked
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    Optional<Artist> findByName(String name);
}

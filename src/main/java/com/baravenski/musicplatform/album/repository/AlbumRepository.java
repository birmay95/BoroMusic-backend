package com.baravenski.musicplatform.album.repository;

import com.baravenski.musicplatform.album.model.Album;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@NullMarked
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    Optional<Album> findByTitle(String title);
}

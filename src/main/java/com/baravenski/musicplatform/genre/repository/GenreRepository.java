package com.baravenski.musicplatform.genre.repository;

import com.baravenski.musicplatform.genre.model.Genre;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@NullMarked
@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {

    Optional<Genre> findByName(String name);
}

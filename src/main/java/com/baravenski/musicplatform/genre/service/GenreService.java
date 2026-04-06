package com.baravenski.musicplatform.genre.service;

import com.baravenski.musicplatform.genre.mapper.GenreMapper;
import com.baravenski.musicplatform.genre.model.Genre;
import com.baravenski.musicplatform.genre.repository.GenreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

@Service
@NullMarked
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Transactional
    public Genre getOrCreateGenre(String name) {
        return genreRepository.findByName(name)
                .orElseGet(() -> {
                    var genreToSave = genreMapper.toGenre(name);
                    return genreRepository.save(genreToSave);
                });
    }
}

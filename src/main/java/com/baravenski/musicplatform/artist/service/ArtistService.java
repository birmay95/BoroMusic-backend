package com.baravenski.musicplatform.artist.service;

import com.baravenski.musicplatform.artist.mapper.ArtistMapper;
import com.baravenski.musicplatform.artist.model.Artist;
import com.baravenski.musicplatform.artist.repository.ArtistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

@Service
@NullMarked
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    private final ArtistMapper artistMapper;

    @Transactional
    public Artist getOrCreateArtist(String name) {
        return artistRepository.findByName(name)
                .orElseGet(() -> {
                    var artistToSave = artistMapper.toArtist(name);
                    return artistRepository.save(artistToSave);
                });
    }
}

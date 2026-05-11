package com.baravenski.musicplatform.artist.service;

import com.baravenski.musicplatform.artist.mapper.ArtistMapper;
import com.baravenski.musicplatform.artist.model.Artist;
import com.baravenski.musicplatform.artist.repository.ArtistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional
    public List<Artist> getOrCreateArtists(String artistsString) {
        if (artistsString == null || artistsString.isBlank()) {
            return List.of(getOrCreateArtist("Unknown Artist"));
        }

        String[] artistNames = artistsString.split("[,&]|\\s+(?i)(feat\\.?|ft\\.?)\\s+");

        return java.util.Arrays.stream(artistNames)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(this::getOrCreateArtist)
                .toList();
    }
}

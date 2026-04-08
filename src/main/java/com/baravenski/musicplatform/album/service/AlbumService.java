package com.baravenski.musicplatform.album.service;

import com.baravenski.musicplatform.album.mapper.AlbumMapper;
import com.baravenski.musicplatform.album.model.Album;
import com.baravenski.musicplatform.album.repository.AlbumRepository;
import com.baravenski.musicplatform.artist.model.Artist;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

@Service
@NullMarked
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    private final AlbumMapper albumMapper;

    @Transactional
    public Album getOrCreateAlbum(String title, Artist artist) {
        return albumRepository.findByTitle(title)
                .orElseGet(() -> {
                    var albumToSave = albumMapper.toAlbum(title, artist);
                    return albumRepository.save(albumToSave);
                });
    }
}

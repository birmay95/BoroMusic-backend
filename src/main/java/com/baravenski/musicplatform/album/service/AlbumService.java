package com.baravenski.musicplatform.album.service;

import com.baravenski.musicplatform.album.mapper.AlbumMapper;
import com.baravenski.musicplatform.album.model.Album;
import com.baravenski.musicplatform.album.repository.AlbumRepository;
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
    public Album getOrCreateAlbum(String title) {
        return albumRepository.findByTitle(title)
                .orElseGet(() -> {
                    var albumToSave = albumMapper.toAlbum(title);
                    return albumRepository.save(albumToSave);
                });
    }
}

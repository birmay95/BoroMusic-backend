package com.baravenski.musicplatform.album.mapper;

import com.baravenski.musicplatform.album.model.Album;
import com.baravenski.musicplatform.artist.model.Artist;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@NullMarked
@Mapper(componentModel = "spring")
public interface AlbumMapper {

    @Mapping(target = "artist", source = "artist")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Album toAlbum(String title, Artist artist);
}

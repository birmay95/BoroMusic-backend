package com.baravenski.musicplatform.album.mapper;

import com.baravenski.musicplatform.album.model.Album;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    Album toAlbum(String title);
}

package com.baravenski.musicplatform.artist.mapper;

import com.baravenski.musicplatform.artist.model.Artist;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;

@NullMarked
@Mapper(componentModel = "spring")
public interface ArtistMapper {

    Artist toArtist(String name);
}

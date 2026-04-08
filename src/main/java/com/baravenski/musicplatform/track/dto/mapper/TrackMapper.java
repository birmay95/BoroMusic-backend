package com.baravenski.musicplatform.track.dto.mapper;

import com.baravenski.musicplatform.genre.mapper.GenreMapper;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.track.model.Track;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = GenreMapper.class)
public interface TrackMapper {

    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenresToStrings")
    @Mapping(target = "artist", source = "artist.name")
    @Mapping(target = "album", source = "album.title")
    @Mapping(target = "uploadedBy", source = "uploadedBy.id")
    TrackResponseDto toDto(Track track);

    List<TrackResponseDto> toDtoList(List<Track> tracks);
}
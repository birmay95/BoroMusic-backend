package com.baravenski.musicplatform.track.dto.mapper;

import com.baravenski.musicplatform.artist.model.Artist;
import com.baravenski.musicplatform.genre.mapper.GenreMapper;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.track.model.Track;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = GenreMapper.class)
public interface TrackMapper {

    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenresToStrings")
    @Mapping(target = "artist", expression = "java(mapArtistsToString(track.getArtists()))")
    @Mapping(target = "album", source = "album.title")
    @Mapping(target = "uploadedBy", source = "uploadedBy.id")
    TrackResponseDto toDto(Track track);

    List<TrackResponseDto> toDtoList(List<Track> tracks);

    default String mapArtistsToString(List<Artist> artists) {
        if (artists == null || artists.isEmpty()) {
            return "Unknown Artist";
        }
        return artists.stream()
                .map(Artist::getName)
                .collect(Collectors.joining(", "));
    }
}
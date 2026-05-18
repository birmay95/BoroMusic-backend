package com.baravenski.musicplatform.playlist.dto.mapper;

import com.baravenski.musicplatform.playlist.dto.PlaylistRequestDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistResponseDto;
import com.baravenski.musicplatform.playlist.dto.PlaylistWithTracksResponseDto;
import com.baravenski.musicplatform.playlist.model.Playlist;
import com.baravenski.musicplatform.track.dto.mapper.TrackMapper;
import com.baravenski.musicplatform.user.service.UserService;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@NullMarked
@Mapper(componentModel = "spring", uses = {TrackMapper.class, UserService.class})
public interface PlaylistMapper {

    PlaylistWithTracksResponseDto toDtoWithTracks(Playlist playlist);

    PlaylistResponseDto toDto(Playlist playlist);

    List<PlaylistResponseDto> toDtoList(List<Playlist> playlists);

    @Mapping(source = "userId", target = "user", qualifiedByName = "findUserById")
    Playlist toEntity(PlaylistRequestDto playlistRequestDto);
}
package com.baravenski.musicplatform.artistrequest.dto.mapper;

import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestCreateDto;
import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestResponseDto;
import com.baravenski.musicplatform.artistrequest.model.ArtistRequest;
import com.baravenski.musicplatform.user.model.User;
import com.baravenski.musicplatform.user.service.UserService;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@NullMarked
@Mapper(componentModel = "spring")
public interface ArtistRequestMapper {

    @Mapping(source = "userId", target = "user")
    @Mapping(target = "status", constant = "PENDING")
    ArtistRequest toEntity(ArtistRequestCreateDto dto);

    // TODO check this method if user needed (now without user)
    ArtistRequestResponseDto toDto(ArtistRequest artistRequest);

    List<ArtistRequestResponseDto> toDtoList(List<ArtistRequest> artistRequests);

    default User mapUserFromId(UUID id) {
        var user = new User();
        user.setId(id);
        return user;
    }
}
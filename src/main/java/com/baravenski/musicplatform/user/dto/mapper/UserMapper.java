package com.baravenski.musicplatform.user.dto.mapper;

import com.baravenski.musicplatform.auth.dto.AuthRegister;
import com.baravenski.musicplatform.core.security.enums.UserRoles;
import com.baravenski.musicplatform.user.dto.UserResponseDto;
import com.baravenski.musicplatform.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "favourites", ignore = true)
    @Mapping(target = "playlists", ignore = true)
    User toEntity(AuthRegister authRegister, String encodedPassword);

    @Named("roleToString")
    default String roleToString(UserRoles role) {
        return role.name();
    }
}
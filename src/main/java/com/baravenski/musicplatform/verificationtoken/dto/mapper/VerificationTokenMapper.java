package com.baravenski.musicplatform.verificationtoken.dto.mapper;

import com.baravenski.musicplatform.verificationtoken.dto.VerificationTokenDto;
import com.baravenski.musicplatform.verificationtoken.model.VerificationToken;
import com.baravenski.musicplatform.user.model.User;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Random;

@NullMarked
@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface VerificationTokenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "token", expression = "java(generateToken())")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "expiryDate", expression = "java(LocalDateTime.now().plusHours(24))")
    VerificationToken toEntityFromUser(User user);

    VerificationTokenDto toDto(VerificationToken verificationToken);

    default String generateToken() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}

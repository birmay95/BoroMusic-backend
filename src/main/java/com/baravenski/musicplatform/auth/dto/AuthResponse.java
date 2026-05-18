package com.baravenski.musicplatform.auth.dto;

import com.baravenski.musicplatform.user.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.NullMarked;

@Data
@NullMarked
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}

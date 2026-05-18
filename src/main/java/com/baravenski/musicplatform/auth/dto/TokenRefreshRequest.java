package com.baravenski.musicplatform.auth.dto;

import lombok.Data;
import org.jspecify.annotations.NullMarked;

@Data
@NullMarked
public class TokenRefreshRequest {
    private String refreshToken;
}

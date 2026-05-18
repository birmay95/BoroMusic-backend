package com.baravenski.musicplatform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.NullMarked;

@Data
@NullMarked
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}

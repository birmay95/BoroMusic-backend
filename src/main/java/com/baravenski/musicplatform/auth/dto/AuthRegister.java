package com.baravenski.musicplatform.auth.dto;

import lombok.Data;
import org.jspecify.annotations.NullMarked;

@Data
@NullMarked
public class AuthRegister {
    private String email;
    private String username;
    private String password;
}

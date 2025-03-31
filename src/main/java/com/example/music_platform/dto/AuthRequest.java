package com.example.music_platform.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
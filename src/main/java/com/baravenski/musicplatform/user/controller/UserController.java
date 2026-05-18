package com.baravenski.musicplatform.user.controller;

import com.baravenski.musicplatform.auth.dto.ChangePasswordRequest;
import com.baravenski.musicplatform.user.dto.UserResponseDto;
import com.baravenski.musicplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@NullMarked
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseStatus(OK)
    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(OK)
    @PostMapping("/{id}/change-password")
    public void changePassword(
            @PathVariable UUID id,
            @RequestBody ChangePasswordRequest request) {

        userService.changePassword(id, request);
    }

    @ResponseStatus(OK)
    @PostMapping("/{id}/change-email")
    public void changeEmail(
            @PathVariable UUID id,
            @RequestBody String newEmail) {

        userService.changeEmail(id, newEmail);
    }

    @ResponseStatus(OK)
    @PostMapping("/{id}/favourites/{trackId}")
    public void addFavourite(@PathVariable UUID id, @PathVariable UUID trackId) {
        userService.addFavourite(id, trackId);
    }

    @ResponseStatus(OK)
    @DeleteMapping("/{id}/favourites/{trackId}")
    public void removeFavourite(@PathVariable UUID id, @PathVariable UUID trackId) {
        userService.removeFavourites(id, trackId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUserById(@PathVariable UUID id) {
        userService.deleteUserById(id);
    }
}

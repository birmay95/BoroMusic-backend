package com.example.music_platform.controller;

import com.example.music_platform.dto.ChangePasswordRequest;
import com.example.music_platform.model.Playlist;
import com.example.music_platform.model.Track;
import com.example.music_platform.model.User;
import com.example.music_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {

        String responseMessage = userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/{userId}/change-email")
    public ResponseEntity<String> changeEmail(
            @PathVariable Long userId,
            @RequestBody String newEmail) {

        String responseMessage = userService.changeEmail(userId, newEmail);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/{userId}/favourites/{trackId}")
    public ResponseEntity<String> addFavourite(@PathVariable Long userId, @PathVariable Long trackId) {
        return ResponseEntity.ok(userService.addFavourite(userId, trackId));
    }

    @DeleteMapping("/{userId}/favourites/{trackId}")
    public ResponseEntity<String> removeFavourite(@PathVariable Long userId, @PathVariable Long trackId) {
        return ResponseEntity.ok(userService.removeFavourites(userId, trackId));
    }

    @GetMapping("/{userId}/favourites")
    public ResponseEntity<Set<Track>> getFavourites(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFavourites(userId));
    }

    @GetMapping("/{userId}/playlists")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getPlaylists(userId));
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}

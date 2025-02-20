package com.example.music_platform.controller;

import com.example.music_platform.model.ArtistRequest;
import com.example.music_platform.service.ArtistRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistRequestController {

    private final ArtistRequestService artistRequestService;

    // Отправка заявки на роль артиста
    @PostMapping("/request/{userId}")
    public ResponseEntity<String> requestArtist(@PathVariable Long userId) {
        artistRequestService.requestArtistRole(userId);
        return ResponseEntity.ok("PENDING");
    }

    // Админ одобряет заявку
    @PostMapping("/approve/{requestId}")
    public ResponseEntity<String> approveArtist(@PathVariable Long requestId) {
        artistRequestService.approveArtist(requestId);
        return ResponseEntity.ok("APPROVED");
    }

    // Админ отклоняет заявку
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectArtist(@PathVariable Long requestId) {
        artistRequestService.rejectArtist(requestId);
        return ResponseEntity.ok("REJECTED");
    }

    // Получить список всех заявок (для админов)
    @GetMapping("/requests")
    public ResponseEntity<List<ArtistRequest>> getAllRequests() {
        return ResponseEntity.ok(artistRequestService.getAllRequests());
    }
}


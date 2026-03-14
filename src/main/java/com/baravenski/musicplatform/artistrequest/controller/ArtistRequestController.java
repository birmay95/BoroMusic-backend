package com.baravenski.musicplatform.artistrequest.controller;

import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestCreateDto;
import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestResponseDto;
import com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus;
import com.baravenski.musicplatform.artistrequest.model.ArtistRequest;
import com.baravenski.musicplatform.artistrequest.service.ArtistRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistRequestController {

    private final ArtistRequestService artistRequestService;

    @ResponseStatus(OK)
    @PostMapping("/request")
    public ArtistRequestStatus requestArtist(@RequestBody ArtistRequestCreateDto artistRequestCreateDto) {
        artistRequestService.requestArtistRole(artistRequestCreateDto);
        return ArtistRequestStatus.PENDING;
    }

    @ResponseStatus(OK)
    @PostMapping("/approve/{id}")
    public ArtistRequestStatus approveArtist(@PathVariable UUID id) {
        artistRequestService.approveArtist(id);
        return ArtistRequestStatus.APPROVED;
    }

    @ResponseStatus(OK)
    @PostMapping("/reject/{id}")
    public ArtistRequestStatus rejectArtist(@PathVariable UUID id) {
        artistRequestService.rejectArtist(id);
        return ArtistRequestStatus.REJECTED;
    }

    @ResponseStatus(OK)
    @GetMapping("/requests")
    public List<ArtistRequestResponseDto> getAllRequests() {
        return artistRequestService.getAllRequests();
    }

    @ResponseStatus(OK)
    @GetMapping("/requests/{userId}")
    public ArtistRequestResponseDto getRequestByUserId(@PathVariable UUID userId) {
        return artistRequestService.getRequestByUserId(userId);
    }
}


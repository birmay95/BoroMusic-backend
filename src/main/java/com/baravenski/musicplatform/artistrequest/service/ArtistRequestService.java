package com.baravenski.musicplatform.artistrequest.service;

import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestCreateDto;
import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestResponseDto;
import com.baravenski.musicplatform.artistrequest.dto.mapper.ArtistRequestMapper;
import com.baravenski.musicplatform.exception.impl.ArtistRequestNotFoundByUserIdException;
import com.baravenski.musicplatform.exception.impl.ArtistRequestNotFoundException;
import com.baravenski.musicplatform.artistrequest.repository.ArtistRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus.APPROVED;
import static com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus.PENDING;
import static com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus.REJECTED;
import static com.baravenski.musicplatform.core.security.enums.UserRoles.ARTIST;

@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public class ArtistRequestService {

    private final ArtistRequestRepository artistRequestRepository;

    private final ArtistRequestMapper artistRequestMapper;

    public void requestArtistRole(ArtistRequestCreateDto artistRequestCreateDto) {
        log.info("[ADMIN-PANEL] User ID: {} submitted a request for ARTIST role", artistRequestCreateDto.userId());
        var requestToSave = artistRequestRepository.findByUserId(artistRequestCreateDto.userId())
                .map(existingRequest -> {
                    if (REJECTED.equals(existingRequest.getStatus())) {
                        existingRequest.setStatus(PENDING);
                        return existingRequest;
                    }
                    throw new RuntimeException("Request already submitted and is not rejected");
                })
                .orElseGet(() -> artistRequestMapper.toEntity(artistRequestCreateDto));

        artistRequestRepository.save(requestToSave);
    }

    @Transactional
    public void approveArtist(UUID id) {
        log.info("[ADMIN-PANEL] Administrator is approving ARTIST role for request ID: {}", id);
        var request = artistRequestRepository.findArtistRequestWithUserById(id)
                .orElseThrow(() -> new ArtistRequestNotFoundException(id));
        var user = request.getUser();
        user.setRole(ARTIST);
        request.setStatus(APPROVED);
        artistRequestRepository.save(request);
        log.info("[ADMIN-PANEL] Success! User {} is now an ARTIST.", user.getUsername());
    }

    public void rejectArtist(UUID id) {
        var request = artistRequestRepository.findById(id)
                .orElseThrow(() -> new ArtistRequestNotFoundException(id));
        request.setStatus(REJECTED);
        artistRequestRepository.save(request);
    }

    public List<ArtistRequestResponseDto> getAllRequests() {
        var artistRequests = artistRequestRepository.findArtistRequestsWithUser();
        return artistRequestMapper.toDtoList(artistRequests);
    }

    public ArtistRequestResponseDto getRequestByUserId(UUID userId) {
        var artistRequest = artistRequestRepository.findArtistRequestWithUserById(userId)
                .orElseThrow(() -> new ArtistRequestNotFoundByUserIdException(userId));
        return artistRequestMapper.toDto(artistRequest);
    }

    public void deleteByUserId(UUID userId) {
        artistRequestRepository.deleteByUserId(userId);
    }
}

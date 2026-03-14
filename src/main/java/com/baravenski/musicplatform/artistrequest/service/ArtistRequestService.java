package com.baravenski.musicplatform.artistrequest.service;

import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestCreateDto;
import com.baravenski.musicplatform.artistrequest.dto.ArtistRequestResponseDto;
import com.baravenski.musicplatform.artistrequest.dto.mapper.ArtistRequestMapper;
import com.baravenski.musicplatform.exception.impl.ArtistRequestNotFoundByUserIdException;
import com.baravenski.musicplatform.exception.impl.ArtistRequestNotFoundException;
import com.baravenski.musicplatform.artistrequest.repository.ArtistRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus.APPROVED;
import static com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus.PENDING;
import static com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus.REJECTED;
import static com.baravenski.musicplatform.core.security.enums.UserRoles.ARTIST;

@AllArgsConstructor
@Service
public class ArtistRequestService {

    private final ArtistRequestRepository artistRequestRepository;

    private final ArtistRequestMapper artistRequestMapper;

    public void requestArtistRole(ArtistRequestCreateDto artistRequestCreateDto) {
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

    public void approveArtist(UUID id) {
        var request = artistRequestRepository.findArtistRequestWithUserById(id)
                .orElseThrow(() -> new ArtistRequestNotFoundException(id));
        var user = request.getUser();
        user.setRole(ARTIST);
        request.setStatus(APPROVED);
        artistRequestRepository.save(request);
    }

    public void rejectArtist(UUID id) {
        var request = artistRequestRepository.findById(id)
                .orElseThrow(() -> new ArtistRequestNotFoundException(id));
        request.setStatus(REJECTED);
        artistRequestRepository.save(request);
    }

    public List<ArtistRequestResponseDto> getAllRequests() {
        var artistRequests = artistRequestRepository.findAll();
        return artistRequestMapper.toDtoList(artistRequests);
    }

    public ArtistRequestResponseDto getRequestByUserId(UUID userId) {
        var artistRequest = artistRequestRepository.findByUserId(userId)
                .orElseThrow(() -> new ArtistRequestNotFoundByUserIdException(userId));
        return artistRequestMapper.toDto(artistRequest);
    }

    public void deleteByUserId(UUID userId) {
        artistRequestRepository.deleteByUserId(userId);
    }
}


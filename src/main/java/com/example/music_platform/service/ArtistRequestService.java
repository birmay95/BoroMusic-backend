package com.example.music_platform.service;

import com.example.music_platform.exception.RequestNotFoundException;
import com.example.music_platform.exception.UserNotFoundException;
import com.example.music_platform.model.ArtistRequest;
import com.example.music_platform.model.User;
import com.example.music_platform.repository.ArtistRequestRepository;
import com.example.music_platform.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ArtistRequestService {

    private final ArtistRequestRepository artistRequestRepository;
    private final UserRepository userRepository;

    public void requestArtistRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ArtistRequest existingRequest = artistRequestRepository.findByUserId(userId).orElse(null);

        if (existingRequest != null) {
            if ("REJECTED".equals(existingRequest.getStatus())) {
                existingRequest.setStatus("PENDING");
                artistRequestRepository.save(existingRequest);
            } else {
                throw new RuntimeException("Request already submitted and is not rejected");
            }
        } else {
            ArtistRequest newRequest = new ArtistRequest();
            newRequest.setUser(user);
            newRequest.setStatus("PENDING");

            artistRequestRepository.save(newRequest);
        }
    }

    public void approveArtist(Long requestId) {
        ArtistRequest request = artistRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));

        User user = request.getUser();
        user.setRoles("ARTIST");

        userRepository.save(user);
        request.setStatus("APPROVED");
        artistRequestRepository.save(request);
    }

    public void rejectArtist(Long requestId) {
        ArtistRequest request = artistRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));

        request.setStatus("REJECTED");
        artistRequestRepository.save(request);
    }

    public List<ArtistRequest> getAllRequests() {
        return artistRequestRepository.findAll();
    }

    public ArtistRequest getRequest(Long userId) {
        return artistRequestRepository.findByUserId(userId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));
    }

}


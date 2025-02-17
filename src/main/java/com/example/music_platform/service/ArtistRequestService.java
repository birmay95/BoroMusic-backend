package com.example.music_platform.service;

import com.example.music_platform.model.ArtistRequest;
import com.example.music_platform.model.User;
import com.example.music_platform.repository.ArtistRequestRepository;
import com.example.music_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistRequestService {

    private final ArtistRequestRepository artistRequestRepository;
    private final UserRepository userRepository;

    // Отправка заявки
    public void requestArtistRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (artistRequestRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Request already submitted");
        }

        ArtistRequest request = new ArtistRequest();
        request.setUser(user);
        request.setStatus("PENDING");

        artistRequestRepository.save(request);
    }

    // Администратор одобряет заявку
    public void approveArtist(Long requestId) {
        ArtistRequest request = artistRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        User user = request.getUser();
        user.setRoles("ARTIST");
        user.setIsVerified(true);

        userRepository.save(user);
        request.setStatus("APPROVED");
        artistRequestRepository.save(request);
    }

    // Администратор отклоняет заявку
    public void rejectArtist(Long requestId) {
        ArtistRequest request = artistRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");
        artistRequestRepository.save(request);
    }

    // Получить список заявок (для админ-панели)
    public List<ArtistRequest> getAllRequests() {
        return artistRequestRepository.findAll();
    }
}


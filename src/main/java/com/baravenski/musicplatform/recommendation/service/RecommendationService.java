package com.baravenski.musicplatform.recommendation.service;

import com.baravenski.musicplatform.core.ml.MlService;
import com.baravenski.musicplatform.recommendation.dto.ExcludedTracksRequest;
import com.baravenski.musicplatform.recommendation.dto.RecommendationDto;
import com.baravenski.musicplatform.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public class RecommendationService {

    private final MlService mlService;
    private final TrackService trackService;

    public List<RecommendationDto> getRecommendations(UUID trackId, ExcludedTracksRequest excludedTracksRequest) {
        List<String> excludedIdsStrings = excludedTracksRequest.excludedIds().stream()
                .map(UUID::toString)
                .toList();

        trackService.findTrackById(trackId);
        return mlService.getRecommendations(trackId.toString(), excludedIdsStrings);
    }

    public List<RecommendationDto> getPersonalRecommendations(UUID userId, ExcludedTracksRequest excludedTracksRequest) {
        log.info("[ML-RECOMMENDATION] Generating 'For You' personalized feed for User ID: {}", userId);
        List<String> excludedIdsStrings = excludedTracksRequest.excludedIds().stream()
                .map(UUID::toString)
                .toList();

        final int limitFavourites = 50;
        List<UUID> likedTrackIds = trackService.getRecentFavouriteTrackIds(userId, limitFavourites);
        log.info("[ML-RECOMMENDATION] Found {} recent favourite tracks for centroid calculation", likedTrackIds.size());

        List<String> likedIdsStrings = likedTrackIds.stream().map(UUID::toString).toList();
        log.info("[ML-CLIENT] Sending request to Analytical Module via HTTP REST...");
        return mlService.getPersonalRecommendations(likedIdsStrings, excludedIdsStrings);
    }
}

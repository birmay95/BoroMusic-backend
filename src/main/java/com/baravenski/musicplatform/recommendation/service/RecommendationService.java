package com.baravenski.musicplatform.recommendation.service;

import com.baravenski.musicplatform.core.ml.MlService;
import com.baravenski.musicplatform.recommendation.dto.RecommendationDto;
import com.baravenski.musicplatform.track.service.TrackService;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@NullMarked
@AllArgsConstructor
public class RecommendationService {

    private final MlService mlService;
    private final TrackService trackService;

    public List<RecommendationDto> getRecommendations(UUID trackId) {
        trackService.findTrackById (trackId);

        return mlService.getRecommendations(trackId);
    }
}

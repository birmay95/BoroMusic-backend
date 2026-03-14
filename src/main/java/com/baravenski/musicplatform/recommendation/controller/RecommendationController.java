package com.baravenski.musicplatform.recommendation.controller;

import com.baravenski.musicplatform.recommendation.dto.RecommendationDto;
import com.baravenski.musicplatform.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{trackId}")
    public List<RecommendationDto> getRecommendations(@PathVariable UUID trackId) {
        return recommendationService.getRecommendations(trackId);
    }
}

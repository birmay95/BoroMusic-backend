package com.example.music_platform.controller;

import com.example.music_platform.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{trackId}")
    public List<Map<String, String>> getRecommendations(@PathVariable Long trackId) throws JSONException {
        return recommendationService.getRecommendations(trackId);
    }
}

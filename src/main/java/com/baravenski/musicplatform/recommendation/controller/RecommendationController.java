package com.baravenski.musicplatform.recommendation.controller;

import com.baravenski.musicplatform.recommendation.dto.ExcludedTracksRequest;
import com.baravenski.musicplatform.recommendation.dto.RecommendationDto;
import com.baravenski.musicplatform.recommendation.service.RecommendationService;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.user.model.User;
import com.baravenski.musicplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    @ResponseStatus(OK)
    @PostMapping("/{trackId}")
    public List<TrackResponseDto> getRecommendations(
            @PathVariable UUID trackId,
            @RequestBody(required = false) ExcludedTracksRequest excludedTracksRequest
    ) {
        return recommendationService.getRecommendations(trackId, excludedTracksRequest);
    }

    @ResponseStatus(OK)
    @PostMapping("/personal")
    public List<RecommendationDto> getPersonalRecommendations(
            @RequestBody(required = false) ExcludedTracksRequest excludedTracksRequest
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findUserByEmailOrUsername(username);
        return recommendationService.getPersonalRecommendations(currentUser.getId(), excludedTracksRequest);
    }
}

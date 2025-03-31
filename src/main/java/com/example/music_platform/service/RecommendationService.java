package com.example.music_platform.service;

import com.example.music_platform.exception.TrackNotFoundException;
import com.example.music_platform.repository.TrackRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class RecommendationService {

    private final String ML_URL = Dotenv.load().get("ML_SERVICE_URL_RECS");
    private final RestTemplate restTemplate = new RestTemplate();
    private final TrackRepository trackRepository;


    public List<Map<String, String>> getRecommendations(Long trackId) throws JSONException {
        if(trackRepository.findById(trackId).isEmpty()) {
            throw new TrackNotFoundException("Track not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("track_id", new HttpEntity<>(trackId));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        assert ML_URL != null;
        ResponseEntity<String> response = restTemplate.exchange(
                ML_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return getRecommendationsFromResponse(response);
    }

    @NotNull
    private static List<Map<String, String>> getRecommendationsFromResponse(ResponseEntity<String> response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray recommendedTracks = jsonResponse.getJSONArray("recommendations");

        List<Map<String, String>> recommendations = new ArrayList<>();
        for (int i = 0; i < recommendedTracks.length(); i++) {
            JSONObject track = recommendedTracks.getJSONObject(i);
            Map<String, String> trackMap = new HashMap<>();
            trackMap.put("track_id", track.getString("track_id"));
            trackMap.put("valence", track.getString("valence"));
            trackMap.put("arousal", track.getString("arousal"));
            recommendations.add(trackMap);
        }
        return recommendations;
    }

}

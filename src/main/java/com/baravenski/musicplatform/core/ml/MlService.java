package com.baravenski.musicplatform.core.ml;

import com.baravenski.musicplatform.exception.impl.DeleteTrackToTheMlServiceException;
import com.baravenski.musicplatform.exception.impl.RecommendationException;
import com.baravenski.musicplatform.exception.impl.UploadTrackToTheMlOrAwsServiceException;
import com.baravenski.musicplatform.recommendation.dto.RecommendationDto;
import com.baravenski.musicplatform.recommendation.dto.RecommendationResponseWrapper;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@NullMarked
public class MlService {

    private final RestClient restClient = RestClient.create();

    @Value("${ml.service.url.upload}")
    private String mlUrlUpload;

    @Value("${ml.service.url.delete}")
    private String mlUrlDelete;

    @Value("${ml.service.url.recommendations}")
    private String mlUrlRecs;

    public String uploadTrackData(File file, UUID trackId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("track_id", trackId.toString());

        var response = restClient.post()
                .uri(mlUrlUpload)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(String.class);

        return Optional.ofNullable(response)
                .orElseThrow(UploadTrackToTheMlOrAwsServiceException::new);
    }

    public String deleteTrackData(UUID trackId) {
        Map<String, String> body = Map.of("track_id", trackId.toString());

        var response = restClient.post()
                .uri(mlUrlDelete)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        return Optional.ofNullable(response)
                .orElseThrow(DeleteTrackToTheMlServiceException::new);
    }

    public List<RecommendationDto> getRecommendations(UUID trackId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("track_id", trackId.toString());

        var responseWrapper = restClient.post()
                .uri(mlUrlRecs)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(RecommendationResponseWrapper.class);

        return Optional.ofNullable(responseWrapper)
                .map(RecommendationResponseWrapper::recommendations)
                .orElseThrow(RecommendationException::new);
    }
}

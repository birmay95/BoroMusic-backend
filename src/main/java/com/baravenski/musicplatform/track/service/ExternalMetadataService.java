package com.baravenski.musicplatform.track.service;

import com.baravenski.musicplatform.track.dto.ItunesResponse;
import com.baravenski.musicplatform.track.dto.ItunesTrack;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@NullMarked
public class ExternalMetadataService {

    private final RestClient restClient;

    public ExternalMetadataService() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.valueOf("text/javascript")
        ));

        this.restClient = RestClient.builder()
                .messageConverters(converters -> converters.add(converter))
                .build();
    }

    public Optional<ItunesTrack> fetchMissingMetadata(String artist, String title) {
        try {
            String searchQuery = artist.trim() + " " + title.trim();

            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("itunes.apple.com")
                            .path("/search")
                            .queryParam("term", searchQuery)
                            .queryParam("entity", "song")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .body(ItunesResponse.class);

            if (response != null && response.resultCount() != null && response.resultCount() > 0) {
                ItunesTrack track = response.results().get(0);
                return Optional.of(track);
            }
        } catch (Exception ignored) {
        }

        return Optional.empty();
    }
}

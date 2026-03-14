package com.baravenski.musicplatform.track.service;

import com.baravenski.musicplatform.exception.impl.TrackNotFoundException;
import com.baravenski.musicplatform.exception.impl.UploadTrackParsingException;
import com.baravenski.musicplatform.exception.impl.UploadTrackToTheMlOrAwsServiceException;
import com.baravenski.musicplatform.core.ml.MlService;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.track.dto.mapper.TrackMapper;
import com.baravenski.musicplatform.track.model.Track;
import com.baravenski.musicplatform.track.repository.TrackRepository;
import com.baravenski.musicplatform.core.cloud.service.BackblazeFileService;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final BackblazeFileService backblazeFileService;
    private final TrackMapper trackMapper;
    private final TrackParser trackParser;
    private final MlService mlService;
    private final TransactionTemplate transactionTemplate;

    @Cacheable(value = "track", key = "#id")
    public TrackResponseDto getTrackById(UUID id) {
        var trackById = trackRepository.findTrackWithGenresById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));

        return trackMapper.toDto(trackById);
    }

    public Track findTrackById(UUID id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));
    }

    @Cacheable("tracks")
    public List<TrackResponseDto> getAllTracks() {
        var tracks = trackRepository.findAllWithGenres();
        return trackMapper.toDtoList(tracks);
    }

    public List<TrackResponseDto> getTracksByPlaylistId(UUID playlistId) {
        var tracksByPlaylistId = trackRepository.findTracksByPlaylistId(playlistId);
        return trackMapper.toDtoList(tracksByPlaylistId);
    }

    @CachePut(value = "track", key = "#result.id()")
    @CacheEvict(value = "tracks", allEntries = true)
    public TrackResponseDto uploadTrack(MultipartFile multipartFile) {
        var tempFile = Try.of(() -> {
            final var tempFilenamePrefix = "tempAudioFile";
            var tempAudioFile = File.createTempFile(tempFilenamePrefix, multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempAudioFile);
            return tempAudioFile;
        }).getOrElseThrow(exception -> new UploadTrackParsingException());
        var track = trackParser.parseTrack(tempFile, multipartFile);
        trackRepository.save(track);

        Try.run(() -> {
            mlService.uploadTrackData(tempFile, track.getId());
            backblazeFileService.uploadFile(multipartFile.getOriginalFilename(), tempFile.getAbsolutePath());
        }).onFailure(exception -> {
            log.error("Failed to upload track with name {}", track.getTitle(), exception);
            trackRepository.deleteById(track.getId());
            throw new UploadTrackToTheMlOrAwsServiceException();
        }).andFinally(() -> {
            if (!tempFile.delete()) {
                log.error("Temp file cannot be deleted with name: {}", tempFile.getAbsolutePath());
            }
        });

        return trackMapper.toDto(track);
    }

    public List<TrackResponseDto> uploadTracks(List<MultipartFile> files) {
        List<TrackResponseDto> uploadTracks = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadTracks.add(uploadTrack(file));
        }
        return uploadTracks;
    }

    @Caching(evict = {
            @CacheEvict(value = "tracks", allEntries = true),
            @CacheEvict(value = "track", key = "#id")
    })
    public void deleteTrack(UUID id) {
        var track = trackRepository.findById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));

        mlService.deleteTrackData(id);
        backblazeFileService.deleteFile(track.getFileName());

        transactionTemplate.executeWithoutResult(status -> {
            trackRepository.deleteTrackFromFavourites(id);
            trackRepository.deleteTrackGenres(id);
            trackRepository.deleteTrackFromPlaylists(id);
            trackRepository.deleteById(id);
        });
    }

    public void downloadAndSaveFile(String fileName, String savePath) throws IOException {
        String fullSavePath = savePath.endsWith("/") ? savePath + fileName : savePath + "/" + fileName;
        File targetFile = new File(fullSavePath);

        try (InputStream fileStream = backblazeFileService.downloadFileStream(fileName);
             FileOutputStream outStream = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public InputStream downloadFileStream(String fileName) {
        return trackRepository.findByFileName(fileName)
                .map(track -> backblazeFileService.downloadFileStream(fileName))
                .orElseThrow(() -> new TrackNotFoundException(fileName));
    }

    public List<TrackResponseDto> getFavouritesTracksByUserId(UUID userId) {
        var tracks = trackRepository.findTracksByUserId(userId);
        return trackMapper.toDtoList(tracks);
    }
}

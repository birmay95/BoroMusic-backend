package com.baravenski.musicplatform.track.service;

import com.baravenski.musicplatform.core.pagination.PageResponseDto;
import com.baravenski.musicplatform.core.security.enums.UserRoles;
import com.baravenski.musicplatform.exception.impl.AccessDeniedException;
import com.baravenski.musicplatform.exception.impl.TrackNotFoundException;
import com.baravenski.musicplatform.exception.impl.UploadTrackParsingException;
import com.baravenski.musicplatform.exception.impl.UploadTrackToTheMlOrAwsServiceException;
import com.baravenski.musicplatform.core.ml.MlService;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.track.dto.mapper.TrackMapper;
import com.baravenski.musicplatform.track.model.Track;
import com.baravenski.musicplatform.track.repository.TrackRepository;
import com.baravenski.musicplatform.core.cloud.service.BackblazeFileService;
import com.baravenski.musicplatform.user.model.User;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final BackblazeFileService backblazeFileService;
    private final TrackMapper trackMapper;
    private final TrackParser trackParser;
    private final MlService mlService;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    @Cacheable(value = "track", key = "#id")
    public TrackResponseDto getTrackById(UUID id) {
        var trackById = trackRepository.findTrackWithDetailsById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));

        fetchGenresForTracks(List.of(trackById));

        return trackMapper.toDto(trackById);
    }

    public Track findTrackById(UUID id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));
    }

    @Transactional
    @Cacheable(value = "tracks_page", key = "#page")
    public PageResponseDto<TrackResponseDto> getAllTracks(int page) {
        log.info("[LIBRARY] Fetching global track library (Page: {})", page);
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id"));
        var tracksPage = trackRepository.findAllWithDetails(pageable);
        fetchGenresForTracks(tracksPage.getContent());
        var dtoPage = tracksPage.map(trackMapper::toDto);
        log.info("[LIBRARY] Retrieved {} tracks from database", tracksPage.getNumberOfElements());
        return new PageResponseDto<>(dtoPage);
    }

    public List<TrackResponseDto> getTracksByPlaylistId(UUID playlistId) {
        var tracksByPlaylistId = trackRepository.findTracksByPlaylistId(playlistId);
        return trackMapper.toDtoList(tracksByPlaylistId);
    }

    @CachePut(value = "track", key = "#result.id()")
    @CacheEvict(value = "tracks_page", allEntries = true)
    public TrackResponseDto uploadTrack(MultipartFile multipartFile, User currentUser) {
        var tempFile = Try.of(() -> {
            final var tempFilenamePrefix = "tempAudioFile";
            var tempAudioFile = File.createTempFile(tempFilenamePrefix, multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempAudioFile);
            return tempAudioFile;
        }).getOrElseThrow(exception -> new UploadTrackParsingException());
        var track = trackParser.parseTrack(tempFile, multipartFile);
        track.setUploadedBy(currentUser);
        trackRepository.save(track);
        log.info("Step 0: Primary track record saved in DB with ID: {}", track.getId());

        Try.run(() -> {
            log.info("Step 1: Initiating ML feature extraction...");
            mlService.uploadTrackData(tempFile, track.getId());

            log.info("Step 2: Initiating Cloud Storage upload...");
            backblazeFileService.uploadFile(multipartFile.getOriginalFilename(), tempFile.getAbsolutePath());

        }).onFailure(exception -> {
            log.error("CRITICAL: Failed to complete upload chain for track ID: {}. Reason: {}", track.getId(), exception.getMessage());

            log.warn("Initiating compensatory transaction for track ID: {}", track.getId());

            try {
                mlService.deleteTrackData(track.getId());
                log.info("Compensation: ML features deleted for track ID: {}", track.getId());
            } catch (Exception e) {
                log.warn("Compensation: ML cleanup skipped (ML service offline)");
            }

            trackRepository.deleteById(track.getId());
            log.info("Compensation: PostgreSQL metadata deleted for track ID: {}", track.getId());

            throw new UploadTrackToTheMlOrAwsServiceException();

        }).andFinally(() -> {
            if (!tempFile.delete()) {
                log.error("Temp file cleanup failed: {}", tempFile.getAbsolutePath());
            }
        });

        log.info("Step 3: Finished uploading track ID: {}", track.getId());
        return trackMapper.toDto(track);
    }

    public List<TrackResponseDto> uploadTracks(List<MultipartFile> files, User cuurentUser) {
        List<TrackResponseDto> uploadTracks = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadTracks.add(uploadTrack(file, cuurentUser));
        }
        return uploadTracks;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tracks_page", allEntries = true),
            @CacheEvict(value = "track", key = "#id")
    })
    public void deleteTrack(UUID id, User currentUser) {
        var track = trackRepository.findById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));

        boolean isAdmin = currentUser.getRole() == UserRoles.ADMIN;
        boolean isOwner = track.getUploadedBy().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException();
        }

        mlService.deleteTrackData(id);
        backblazeFileService.deleteFile(track.getFileName());

        transactionTemplate.executeWithoutResult(status -> {
            trackRepository.deleteTrackFromFavourites(id);
            trackRepository.deleteTrackGenres(id);
            trackRepository.deleteTrackArtists(id);
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

    @Transactional
    public List<TrackResponseDto> getFavouritesTracksByUserId(UUID userId) {
        var tracks = trackRepository.findTracksByUserId(userId);
        fetchGenresForTracks(tracks);
        return trackMapper.toDtoList(tracks);
    }

    public List<UUID> getRecentFavouriteTrackIds(UUID userId, int limit) {
        return trackRepository.findRecentFavouriteTrackIds(userId, limit);
    }

    public void fetchGenresForTracks(List<Track> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }
        trackRepository.fetchGenresForTracks(tracks);
    }
}

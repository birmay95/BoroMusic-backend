package com.baravenski.musicplatform.track.controller;

import com.baravenski.musicplatform.core.pagination.PageResponseDto;
import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.core.cloud.service.BackblazeFileService;
import com.baravenski.musicplatform.track.service.TrackService;
import com.baravenski.musicplatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@NullMarked
@RestController
@RequestMapping("api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;
    private final BackblazeFileService backblazeFileService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(OK)
    public PageResponseDto<TrackResponseDto> getAllTracks(@RequestParam(defaultValue = "0") int page) {
        return trackService.getAllTracks(page);
    }

    @ResponseStatus(OK)
    @GetMapping("/{id}")
    public TrackResponseDto getTrackById(@PathVariable UUID id) {
        return trackService.getTrackById(id);
    }

    @PostMapping(value = "/upload")
    public TrackResponseDto uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = userService.findUserByEmailOrUsername(username);
        return trackService.uploadTrack(file, currentUser);
    }

    @ResponseStatus(OK)
    @PostMapping(value = "/upload/files")
    public List<TrackResponseDto> uploadFiles(
            @RequestParam("file") List<MultipartFile> files
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = userService.findUserByEmailOrUsername(username);
        return trackService.uploadTracks(files, currentUser);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTrack(@PathVariable UUID id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = userService.findUserByEmailOrUsername(username);
        trackService.deleteTrack(id, currentUser);
    }

    @ResponseStatus(OK)
    @GetMapping("/temporary-url")
    public String getTemporaryUrl(@RequestParam("fileName") String fileName) {
        log.info("[STREAM] Client requested playback access for file: {}", fileName);

        String temporaryUrl = backblazeFileService.generateTemporaryUrl(fileName);
        log.info("[STREAM] Returning secure temporary S3 link to mobile client");

        return temporaryUrl;
    }

    @GetMapping("/buckets")
    public ResponseEntity<String> listBuckets() {
        try {
            backblazeFileService.listBuckets();
            return ResponseEntity.ok("Buckets listed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error listing buckets: " + e.getMessage());
        }
    }

    // An endpoint for downloading a file as a stream and saving it to disk
    @GetMapping("/download-and-save")
    public ResponseEntity<Object> downloadFile(@RequestParam String path,
                                               @RequestParam String fileName) {
        String downloadPath = path + "/" + fileName;

        try {
            backblazeFileService.downloadFile(fileName, downloadPath);

            File file = new File(downloadPath);
            if (!file.exists()) {
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileBytes = fileInputStream.readAllBytes();
            fileInputStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileBytes.length)
                    .body(fileBytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading file: " + e.getMessage());
        }
    }

    // An endpoint saves the file to disk
    @GetMapping("/download")
    public ResponseEntity<String> downloadAndSaveFile(
            @RequestParam String fileName,
            @RequestParam String path) throws IOException {
        trackService.downloadAndSaveFile(fileName, path);
        return ResponseEntity.ok("The file was saved successfully on the way: " + path);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFileStream(
            @PathVariable String fileName) {
        var fileStream = trackService.downloadFileStream(fileName);
        var encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedFileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(fileStream));
    }

    @ResponseStatus(OK)
    @GetMapping("/users/{userId}/favourites")
    public List<TrackResponseDto> getFavouritesTracksByUserId(@PathVariable UUID userId) {
        return trackService.getFavouritesTracksByUserId(userId);
    }
}

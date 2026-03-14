package com.baravenski.musicplatform.track.controller;

import com.baravenski.musicplatform.track.dto.TrackResponseDto;
import com.baravenski.musicplatform.core.cloud.service.BackblazeFileService;
import com.baravenski.musicplatform.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
@CrossOrigin
public class TrackController {

    private final TrackService trackService;
    private final BackblazeFileService backblazeFileService;

    @GetMapping
    @ResponseStatus(OK)
    public List<TrackResponseDto> getAllTracks() {
        return trackService.getAllTracks();
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
        return trackService.uploadTrack(file);
    }

    @ResponseStatus(OK)
    @PostMapping(value = "/upload/files")
    public List<TrackResponseDto> uploadFiles(
            @RequestParam("file") List<MultipartFile> files
    ) {
        return trackService.uploadTracks(files);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTrack(@PathVariable UUID id) {
        trackService.deleteTrack(id);
    }

    @ResponseStatus(OK)
    @GetMapping("/temporary-url")
    public String getTemporaryUrl(@RequestParam("fileName") String fileName) {
        return backblazeFileService.generateTemporaryUrl(fileName);
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

package com.example.music_platform.controller;

import com.example.music_platform.model.Track;
import com.example.music_platform.service.BackblazeFileService;
import com.example.music_platform.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
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
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
@CrossOrigin
public class TrackController {

    private final TrackService trackService;
    private final BackblazeFileService backblazeFileService;

    @GetMapping("/{trackId}")
    public ResponseEntity<Track> getTrack(@PathVariable Long trackId) {
        Track track = trackService.getTrack(trackId);
        return ResponseEntity.ok(track);
    }

    @GetMapping
    public ResponseEntity<List<Track>> getTracks() {
        List<Track> tracks = trackService.getTracks();
        return ResponseEntity.ok(tracks);
    }

    @PostMapping(value = "/upload")
    public Track uploadFile(@RequestParam("userId") Long userId, @RequestParam("file") MultipartFile file) throws IOException, JSONException {
        return trackService.uploadTrack(userId, file);
    }

    @PostMapping(value = "/upload/files")
    public List<Track> uploadFiles(@RequestParam("userId") Long userId, @RequestParam("file") List<MultipartFile> files) throws IOException, JSONException {
        return trackService.uploadTracks(userId, files);
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<String> deleteTrack(@PathVariable Long trackId) {
        String result = trackService.deleteTrack(trackId);
        return ResponseEntity.ok(result);
    }


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
        try {
            InputStream fileStream = trackService.downloadFileStream(fileName);

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encodedFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

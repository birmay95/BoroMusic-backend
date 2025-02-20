package com.example.music_platform.controller;

import com.example.music_platform.model.Track;
import com.example.music_platform.service.BackblazeFileService;
import com.example.music_platform.service.StorageService;
import com.example.music_platform.service.TrackService;
import lombok.AllArgsConstructor;
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
import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping("/tracks")
@CrossOrigin
public class TrackController {

    private final StorageService storageService;

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

    // Эндпоинт для загрузки файлов
    @PostMapping( value = "/upload")
    public Track uploadFile(@RequestParam("userId") Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        return trackService.uploadTrack(userId, file);
    }

    // Эндпоинт для загрузки файлов
    @PostMapping( value = "/upload/files")
    public List<Track> uploadFiles(@RequestParam("userId") Long userId, @RequestParam("file") List<MultipartFile> files) throws IOException {
        return trackService.uploadTracks(userId, files);
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<String> deleteTrack(@PathVariable Long trackId) {
        String result = trackService.deleteTrack(trackId);
        return ResponseEntity.ok(result);
    }


    // Эндпоинт для генерации временной ссылки на файл
    @GetMapping("/temporary-url")
    public String getTemporaryUrl(@RequestParam("fileName") String fileName) {
        return storageService.generateTemporaryUrl(fileName);
    }

    // Эндпоинт для получения списка бакетов
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

//    // Эндпоинт для скачивания файла потоково с сохранением на диск
    @GetMapping("/download-and-save")
    public ResponseEntity<Object> downloadFile(@RequestParam String path,
                                               @RequestParam String fileName) {
        String downloadPath = path + "/" + fileName;

        try {
            // Скачивание файла из бакета
            backblazeFileService.downloadFile(fileName, downloadPath);

            // Подготовка ответа с файлом
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

    // сохраняет файл на диск
    @GetMapping("/download")
    public ResponseEntity<String> downloadAndSaveFile(
            @RequestParam String fileName,
            @RequestParam String path) {
        try {
            // Вызываем сервис для скачивания и сохранения файла
            trackService.downloadAndSaveFile(fileName, path);

            return ResponseEntity.ok("Файл успешно сохранен по пути: " + path);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при сохранении файла: " + e.getMessage());
        }
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFileStream(
            @PathVariable String fileName) {
        try {
            InputStream fileStream = trackService.downloadFileStream(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/stream-url/{fileName}")
//    public ResponseEntity<Map<String, String>> getStreamUrl(@PathVariable String fileName) {
//        Optional<Track> track = trackRepository.findByFileName(fileName);
//
//        if (track.isPresent()) {
//            String streamUrl = "https://yourserver.com/download/" + fileName; // Генерируем URL потока
//            Map<String, String> response = new HashMap<>();
//            response.put("streamingUrl", streamUrl);
//
//            return ResponseEntity.ok(response);
//        }
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Track not found"));
//    }

}

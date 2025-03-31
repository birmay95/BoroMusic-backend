package com.example.music_platform.service;

import com.example.music_platform.exception.TrackNotFoundException;
import com.example.music_platform.exception.UserNotFoundException;
import com.example.music_platform.model.Genre;
import com.example.music_platform.model.Playlist;
import com.example.music_platform.model.Track;
import com.example.music_platform.model.User;
import com.example.music_platform.repository.GenreRepository;
import com.example.music_platform.repository.PlaylistRepository;
import com.example.music_platform.repository.TrackRepository;
import com.example.music_platform.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.List;

@AllArgsConstructor
@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final BackblazeFileService backblazeFileService;
    private final GenreRepository genreRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final String ML_URL_UPLOAD = Dotenv.load().get("ML_SERVICE_URL_UPLOAD");
    private final String ML_URL_DELETE = Dotenv.load().get("ML_SERVICE_URL_DELETE");
    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(value = "track", key = "#trackId")
    public Track getTrack(Long trackId) {
        return trackRepository.findTrackWithGenresById(trackId)
                .orElseThrow(() -> new TrackNotFoundException("Track not found"));
    }

    @Cacheable("tracks")
    public List<Track> getTracks() {
        return trackRepository.findAllWithGenres();
    }

    public Track parseTrack(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            long trackLength = audioHeader.getTrackLength();
            Tag tag = audioFile.getTag();

            String genreString = tag.getFirst(FieldKey.GENRE);
            String artist = tag.getFirst(FieldKey.ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);
            String title = tag.getFirst(FieldKey.TITLE);

            Set<Genre> genres = new HashSet<>();
            if (genreString != null && !genreString.isEmpty()) {
                String[] genreArray = genreString.split("[,;]\\s*");
                for (String genreName : genreArray) {
                    genreName = genreName.trim();
                    Optional<Genre> genreOpt = genreRepository.findByName(genreName);
                    String finalGenreName = genreName;
                    Genre genre = genreOpt.orElseGet(() -> genreRepository.save(new Genre(null, finalGenreName, null)));
                    genres.add(genre);
                }
            }

            if (genres.isEmpty()) {
                Optional<Genre> genreOptional = genreRepository.findByName("Unknown");
                Genre genre = genreOptional.orElseGet(() -> genreRepository.save(new Genre(null, "Unknown", null)));
                genres.add(genre);
            }

            return new Track(null, title, artist, album, "", "", null, trackLength, genres, null); // Заглушки для остальных параметров;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @CachePut(value = "track", key = "#result.id")
    @CacheEvict(value = "tracks", allEntries = true)
    public Track uploadTrack(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!"ARTIST".equals(user.getRoles()) && !"ADMIN".equals(user.getRoles())) {
            throw new RuntimeException("Only verified artists can upload tracks");
        }

        File tempFile = File.createTempFile("tempAudioFile", file.getOriginalFilename());
        file.transferTo(tempFile);

        Track track = parseTrack(tempFile);
        assert track != null;
        track.setContentType(file.getContentType());
        track.setFileSize(file.getSize());
        track.setFileName(file.getOriginalFilename());

        trackRepository.saveAndFlush(track);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile));
        body.add("track_id", new HttpEntity<>(track.getId().toString()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        assert ML_URL_UPLOAD != null;
        ResponseEntity<String> response = restTemplate.exchange(
                ML_URL_UPLOAD,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        String result = backblazeFileService.uploadFile(file.getOriginalFilename(), tempFile.getAbsolutePath());

        if (!tempFile.delete()) {
            System.err.println("Не удалось удалить временный файл: " + tempFile.getAbsolutePath());
        }

        return track;
    }

    public List<Track> uploadTracks(Long userId, List<MultipartFile> files) throws IOException, JSONException {
        List<Track> uploadResults = new ArrayList<>();

        for (MultipartFile file : files) {
            uploadResults.add(uploadTrack(userId, file));
        }
        return uploadResults;
    }

    @Caching(evict = {
            @CacheEvict(value = "tracks", allEntries = true),
            @CacheEvict(value = "track", key = "#trackId")
    })
    @Transactional
    public String deleteTrack(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException("Track not found"));

        Iterator<Playlist> iterator = track.getPlaylists().iterator();
        while (iterator.hasNext()) {
            Playlist playlist = iterator.next();
            iterator.remove();
            playlist.getTracks().remove(track);
            playlistRepository.save(playlist);
        }


        List<User> usersWithFavourite = userRepository.findAllByFavouritesContains(track);
        for (User user : usersWithFavourite) {
            user.getFavourites().remove(track);
            userRepository.save(user);
        }

        track.getGenres().clear();
        trackRepository.save(track);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("track_id", trackId.toString());

        assert ML_URL_DELETE != null;
        ResponseEntity<String> response = restTemplate.postForEntity(
                ML_URL_DELETE,
                new HttpEntity<>(requestBody, headers),
                String.class
        );

        String deleteResult = backblazeFileService.deleteFile(track.getFileName());

        trackRepository.delete(track);

        return deleteResult;
    }

    public void downloadFile(String fileName) throws IOException {
        String downloadPath = "./" + fileName;

        backblazeFileService.downloadFile(fileName, downloadPath);
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
        Optional<Track> track = trackRepository.findByFileName(fileName);
        if (track.isPresent()) {
            return backblazeFileService.downloadFileStream(fileName);
        }
        throw new TrackNotFoundException("Track not found");
    }
}

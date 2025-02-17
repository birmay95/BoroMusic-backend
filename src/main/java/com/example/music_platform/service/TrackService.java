package com.example.music_platform.service;

import com.example.music_platform.dto.TrackDTO;
import com.example.music_platform.model.Genre;
import com.example.music_platform.model.Playlist;
import com.example.music_platform.model.Track;
import com.example.music_platform.model.User;
import com.example.music_platform.repository.GenreRepository;
import com.example.music_platform.repository.PlaylistRepository;
import com.example.music_platform.repository.TrackRepository;
import com.example.music_platform.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Transactional
@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final BackblazeFileService backblazeFileService;
    private final GenreRepository genreRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    private TrackDTO convertToDTO(Track track) {
        return new TrackDTO(track.getId(), track.getTitle(), track.getArtist(), track.getAlbum(), track.getFileName(), track.getContentType(), track.getFileSize(), track.getDuration(), track.getGenres());
    }

    public Track getTrack(Long trackId) {
        return trackRepository.findTrackWithGenresById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));
    }

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
            if (genreString != null) {
                String[] genreArray = genreString.split(",");
                for (String genreName : genreArray) {
                    genreName = genreName.trim();
                    Optional<Genre> genreOpt = genreRepository.findByName(genreName);
                    String finalGenreName = genreName;
                    Genre genre = genreOpt.orElseGet(() -> genreRepository.save(new Genre(null, finalGenreName, null)));
                    genres.add(genre);
                }
            }

            System.out.println("Genre is " + genreString);
            System.out.println("Artist is " + artist);
            System.out.println("Album is " + album);
            System.out.println("Title is " + title);
            return new Track(null, title, artist, album, "", "", null, trackLength, genres, null); // Заглушки для остальных параметров;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Track uploadTrack(Long userId, MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("tempAudioFile", file.getOriginalFilename());
        file.transferTo(tempFile);

        Track track = parseTrack(tempFile);
        assert track != null;
        track.setContentType(file.getContentType());
        track.setFileSize(file.getSize());
        track.setFileName(file.getOriginalFilename());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ROLE_ARTIST".equals(user.getRoles()) || !user.getIsVerified()) {
            throw new RuntimeException("Only verified artists can upload tracks");
        }

        trackRepository.save(track);

        String result = backblazeFileService.uploadFile(file.getOriginalFilename(), tempFile.getAbsolutePath());

        tempFile.delete();

        return track;
//        return "OK";
    }

    public List<Track> uploadTracks(Long userId, List<MultipartFile> files) throws IOException {
        List<Track> uploadResults = new ArrayList<>();

        for(MultipartFile file : files) {
            uploadResults.add(uploadTrack(userId, file));
        }
        return uploadResults;
    }

    @Transactional
    public String deleteTrack(Long trackId) {
        // Проверяем, существует ли трек в базе данных
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        // Удаляем трек из всех связанных плейлистов
        for (Playlist playlist : track.getPlaylists()) {
            playlist.getTracks().remove(track);
            playlistRepository.save(playlist); // Сохраняем изменения для каждого плейлиста
        }

        // Очистите связь трека с жанрами
        track.getGenres().clear();
        trackRepository.save(track); // Сохраняем изменения для трека

        // Удаляем файл из облачного хранилища
        String deleteResult = backblazeFileService.deleteFile(track.getFileName());

        // Удаляем сам трек из базы данных
        trackRepository.delete(track);

        return deleteResult;
    }



    public void downloadFile(String fileName) throws IOException {
        String downloadPath = "./" + fileName;

        backblazeFileService.downloadFile(fileName, downloadPath);
    }

    public void downloadAndSaveFile(String fileName, String savePath) throws IOException {
        // Получаем InputStream для файла из BackBlazeFileService
        InputStream fileStream = backblazeFileService.downloadFileStream(fileName);

        String fullSavePath = savePath.endsWith("/") ? savePath + fileName : savePath + "/" + fileName;

        // Создаем выходной файл по указанному пути
        File targetFile = new File(fullSavePath);
        try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            // Читаем данные из потока и записываем в файл
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public InputStream downloadFileStream(String fileName) throws IOException {
        Optional<Track> track = trackRepository.findByFileName(fileName);
        if (track.isPresent()) {
            return backblazeFileService.downloadFileStream(fileName);
        }
        throw new FileNotFoundException("Track not found");
    }

//    public String getURLFileStream(String fileName) throws IOException {
//        Optional<Track> track = trackRepository.findByFileName(fileName);
//
//        if (track.isPresent()) {
//            String streamUrl = "https://yourserver.com/download/" + fileName; // Генерируем URL потока
//            Map<String, String> response = new HashMap<>();
//            response.put("streamingUrl", streamUrl);
//
//            return ResponseEntity.ok(response);
//        }
//    }
}

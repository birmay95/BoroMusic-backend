package com.baravenski.musicplatform.track.service;

import com.baravenski.musicplatform.exception.impl.UploadTrackToTheMlOrAwsServiceException;
import com.baravenski.musicplatform.genre.service.GenreService;
import com.baravenski.musicplatform.genre.model.Genre;
import com.baravenski.musicplatform.track.model.Track;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@NullMarked
@RequiredArgsConstructor
public class TrackParser {

    private final GenreService genreService;

    public Track parseTrack(File file, MultipartFile multipartFile) {
        var audioFile = Try.of(() -> AudioFileIO.read(file))
                .getOrElseThrow(exception -> new UploadTrackToTheMlOrAwsServiceException());
        var audioHeader = audioFile.getAudioHeader();
        long trackLength = audioHeader.getTrackLength();
        var tag = audioFile.getTag();

        var genreString = tag.getFirst(FieldKey.GENRE);
        var artist = tag.getFirst(FieldKey.ARTIST);
        var album = tag.getFirst(FieldKey.ALBUM);
        var title = tag.getFirst(FieldKey.TITLE);

        List<Genre> genres = new ArrayList<>();
        if (genreString != null && !genreString.isEmpty()) {
            var genreArray = genreString.split("[,;]\\s*");
            for (String genreName : genreArray) {
                var genre = genreService.getOrCreateGenre(genreName);
                genres.add(genre);
            }
        }

        return new Track(
                null,
                title,
                artist,
                album,
                Objects.requireNonNull(multipartFile.getOriginalFilename()),
                Objects.requireNonNull(multipartFile.getContentType()),
                multipartFile.getSize(),
                trackLength,
                genres,
                new ArrayList<>()
        );
    }
}

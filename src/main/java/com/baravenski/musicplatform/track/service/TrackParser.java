package com.baravenski.musicplatform.track.service;

import com.baravenski.musicplatform.album.service.AlbumService;
import com.baravenski.musicplatform.artist.service.ArtistService;
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
    private final ArtistService artistService;
    private final AlbumService albumService;

    public Track parseTrack(File file, MultipartFile multipartFile) {
        var audioFile = Try.of(() -> AudioFileIO.read(file))
                .getOrElseThrow(exception -> new UploadTrackToTheMlOrAwsServiceException());
        var audioHeader = audioFile.getAudioHeader();
        long trackLength = audioHeader.getTrackLength();
        var tag = audioFile.getTag();

        var genreString = tag.getFirst(FieldKey.GENRE);
        var artistString = tag.getFirst(FieldKey.ARTIST);
        var artist = artistService.getOrCreateArtist(artistString);
        var albumString = tag.getFirst(FieldKey.ALBUM);
        var album = albumService.getOrCreateAlbum(albumString, artist);
        var title = tag.getFirst(FieldKey.TITLE);

        List<Genre> genres = new ArrayList<>();
        if (genreString != null && !genreString.isEmpty()) {
            var genreArray = genreString.split("[,;]\\s*");
            for (String genreName : genreArray) {
                var genre = genreService.getOrCreateGenre(genreName);
                genres.add(genre);
            }
        }

        return Track.builder()
                .title(title)
                .fileName(Objects.requireNonNull(multipartFile.getOriginalFilename()))
                .contentType(Objects.requireNonNull(multipartFile.getContentType()))
                .fileSize(multipartFile.getSize())
                .duration(trackLength)
                .artist(artist)
                .album(album)
                .genres(genres)
                .build();
    }
}

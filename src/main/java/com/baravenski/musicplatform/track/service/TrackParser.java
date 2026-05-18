package com.baravenski.musicplatform.track.service;

import com.baravenski.musicplatform.album.service.AlbumService;
import com.baravenski.musicplatform.artist.service.ArtistService;
import com.baravenski.musicplatform.exception.impl.UploadTrackToTheMlOrAwsServiceException;
import com.baravenski.musicplatform.genre.service.GenreService;
import com.baravenski.musicplatform.genre.model.Genre;
import com.baravenski.musicplatform.track.dto.ItunesTrack;
import com.baravenski.musicplatform.track.model.Track;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@NullMarked
@RequiredArgsConstructor
public class TrackParser {

    private final GenreService genreService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final ExternalMetadataService externalMetadataService;

    private record ParsedMetadata(String title, String artist) {
    }

    public Track parseTrack(File file, MultipartFile multipartFile) {
        var audioFile = Try.of(() -> AudioFileIO.read(file))
                .getOrElseThrow(exception -> new UploadTrackToTheMlOrAwsServiceException());

        var audioHeader = audioFile.getAudioHeader();
        long trackLength = audioHeader.getTrackLength();

        var tag = audioFile.getTag();
        String title = tag != null ? tag.getFirst(FieldKey.TITLE) : "";
        String artistString = tag != null ? tag.getFirst(FieldKey.ARTIST) : "";
        String albumString = tag != null ? tag.getFirst(FieldKey.ALBUM) : "";
        String genreString = tag != null ? tag.getFirst(FieldKey.GENRE) : "";

        String originalFilename = Objects.requireNonNull(multipartFile.getOriginalFilename());

        if (title.isBlank() || artistString.isBlank()) {
            ParsedMetadata fallbackData = parseFromFilename(originalFilename, title, artistString);
            title = fallbackData.title();
            artistString = fallbackData.artist();
        }

        title = title.isBlank() ? "Unknown Title" : title.trim();
        artistString = artistString.isBlank() ? "Unknown Artist" : artistString.trim();

        if (!title.equals("Unknown Title") && !artistString.equals("Unknown Artist")
                && (albumString.isBlank() || genreString.isBlank())) {

            Optional<ItunesTrack> externalData = externalMetadataService.fetchMissingMetadata(artistString, title);

            if (externalData.isPresent()) {
                ItunesTrack itunes = externalData.get();
                if (albumString.isBlank() && itunes.collectionName() != null) {
                    albumString = itunes.collectionName();
                }
                if (genreString.isBlank() && itunes.primaryGenreName() != null) {
                    genreString = itunes.primaryGenreName();
                }
            }
        }

        albumString = albumString.isBlank() ? "Single" : albumString.trim();

        var artists = artistService.getOrCreateArtists(artistString);
        var album = albumService.getOrCreateAlbum(albumString, artists.get(0));

        List<Genre> genres = new ArrayList<>();
        if (genreString != null && !genreString.isBlank()) {
            var genreArray = genreString.split("[,;/]\\s*");
            for (String genreName : genreArray) {
                if (!genreName.isBlank()) {
                    genres.add(genreService.getOrCreateGenre(genreName.trim()));
                }
            }
        }

        return Track.builder()
                .title(title)
                .fileName(originalFilename)
                .contentType(Objects.requireNonNull(multipartFile.getContentType()))
                .fileSize(multipartFile.getSize())
                .duration(trackLength)
                .artists(artists)
                .album(album)
                .genres(genres)
                .build();
    }

    private ParsedMetadata parseFromFilename(String filename, String currentTitle, String currentArtist) {
        String cleanName = stripExtension(filename);
        String[] parts = cleanName.split("\\s*-\\s*", 2);

        String newTitle = currentTitle;
        String newArtist = currentArtist;

        if (parts.length == 2) {
            if (newArtist.isBlank()) newArtist = parts[0];
            if (newTitle.isBlank()) newTitle = parts[1];
        } else {
            if (newTitle.isBlank()) newTitle = cleanName;
        }

        return new ParsedMetadata(newTitle, newArtist);
    }

    private String stripExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }
}

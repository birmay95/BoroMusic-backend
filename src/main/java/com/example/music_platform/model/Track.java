package com.example.music_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String album;

    @Column(nullable = false)
    private String fileName;  // Имя файла в хранилище

    @Column(nullable = false)
    private String contentType; // Тип контента, например "audio/mpeg"

    @Column(nullable = false)
    private Long fileSize;     // Размер файла в байтах

    @Column(nullable = false)
    private Long duration;    // Длительность трека в секундах

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_genres",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @ManyToMany(mappedBy = "tracks", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Playlist> playlists;

}


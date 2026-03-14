package com.baravenski.musicplatform.user.model;

import com.baravenski.musicplatform.core.security.enums.UserRoles;
import com.baravenski.musicplatform.playlist.model.Playlist;
import com.baravenski.musicplatform.verificationtoken.model.VerificationToken;
import com.baravenski.musicplatform.track.model.Track;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NullMarked
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRoles role;
    private boolean emailVerified;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private VerificationToken verificationToken;

    @ManyToMany
    @JoinTable(
            name = "user_favourites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    private List<Track> favourites;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Playlist> playlists;
}


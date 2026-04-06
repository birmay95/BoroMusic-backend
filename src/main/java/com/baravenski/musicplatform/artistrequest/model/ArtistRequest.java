package com.baravenski.musicplatform.artistrequest.model;

import com.baravenski.musicplatform.artist.model.Artist;
import com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus;
import com.baravenski.musicplatform.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NullMarked
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "artist_requests")
@EntityListeners(AuditingEntityListener.class)
public class ArtistRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private ArtistRequestStatus status;

    private String requestedName;

    private String adminComment;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_artist_id")
    private Artist targetArtist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}

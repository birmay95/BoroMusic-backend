package com.baravenski.musicplatform.artistrequest.model;

import com.baravenski.musicplatform.artistrequest.enums.ArtistRequestStatus;
import com.baravenski.musicplatform.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NullMarked
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "artist_requests")
public class ArtistRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private ArtistRequestStatus status;

    @CreatedDate
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}


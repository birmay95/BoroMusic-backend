package com.baravenski.musicplatform.verificationtoken.repository;

import com.baravenski.musicplatform.verificationtoken.model.VerificationToken;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@NullMarked
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM VerificationToken v WHERE v.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}

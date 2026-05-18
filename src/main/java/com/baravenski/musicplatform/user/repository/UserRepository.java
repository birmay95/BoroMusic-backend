package com.baravenski.musicplatform.user.repository;

import com.baravenski.musicplatform.user.model.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@NullMarked
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favourites WHERE u.id = :userId")
    Optional<User> findUserWithFavourites(@Param("userId") UUID id);

}

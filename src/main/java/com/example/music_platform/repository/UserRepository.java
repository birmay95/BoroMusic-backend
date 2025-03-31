package com.example.music_platform.repository;

import com.example.music_platform.model.Track;
import com.example.music_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.verificationToken " +
            "LEFT JOIN FETCH u.favourites " +
            "LEFT JOIN FETCH u.playlists " +
            "WHERE u.email = :email")
    Optional<User> findWithFavAndPlaylistsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.verificationToken " +
            "LEFT JOIN FETCH u.favourites " +
            "LEFT JOIN FETCH u.playlists " +
            "WHERE u.username = :username")
    Optional<User> findWithFavAndPlaylistsByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.playlists WHERE u.id = :userId")
    Optional<User> findByIdWithPlaylists(@Param("userId") Long userId);

    Optional<User> findById(Long id);
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favourites WHERE u.id = :userId")
    Optional<User> findUserWithFavourites(@Param("userId") Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.playlists WHERE u.id = :userId")
    Optional<User> findUserWithPlaylists(@Param("userId") Long userId);

    List<User> findAllByFavouritesContains(Track track);
}

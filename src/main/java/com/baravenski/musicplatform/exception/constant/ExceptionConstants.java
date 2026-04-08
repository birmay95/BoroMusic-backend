package com.baravenski.musicplatform.exception.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ExceptionConstants {
    public static final String TRACK_NOT_FOUND_BY_ID = "Track not found by id: %s";
    public static final String TRACK_NOT_FOUND_BY_NAME = "Track not found by name: %s";
    public static final String USER_NOT_FOUND = "User not found by id: %s";
    public static final String USER_NOT_FOUND_BY_LOGIN = "User not found by login: %s";
    public static final String PLAYLIST_NOT_FOUND = "Playlist not found by id: %s";
    public static final String VERIFICATION_TOKEN_NOT_FOUND_BY_USER_ID = "Verification token not found by user id: %s";
    public static final String ARTIST_REQUEST_NOT_FOUND = "Artist request not found by id: %s";
    public static final String ARTIST_REQUEST_NOT_FOUND_BY_USER_ID = "Artist request not found by user id: %s";
    public static final String UPLOAD_TO_THE_ML_OR_AWS_SERVICE_EXCEPTION_MESSAGE = "Error uploading track data to the ml or aws service";
    public static final String UPLOAD_TO_THE_ML_SERVICE_EXCEPTION_MESSAGE = "Error uploading track data during parsing track";
    public static final String DELETE_FROM_THE_ML_SERVICE_EXCEPTION_MESSAGE = "Error deleting track data to the ml service";
    public static final String FETCH_RECS_EXCEPTION_MESSAGE = "Error fetching recommendations from the ml service";
    public static final String INCORRECT_VERIFICATION_TOKEN = "Incorrect verification token";
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    public static final String WEAK_PASSWORD = "The password must contain at least 6 characters";
    public static final String USER_WITH_NAME_ALREADY_EXISTS = "A user already exists with name: %s";
    public static final String USER_WITH_EMAIL_ALREADY_EXISTS = "A user already exists with login: %s";
    public static final String BRUTE_FORCE_LOCK = "Brute Force Lock for time: %d";
    public static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token";
    public static final String BAD_CREDENTIALS = "Bad credentials: %s";
    public static final String ACCESS_DENIED = "Access denied to this track, you aren't owner";
}

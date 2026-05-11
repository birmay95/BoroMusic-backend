package com.baravenski.musicplatform.core.security.trackupload;

import com.baravenski.musicplatform.core.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.baravenski.musicplatform.core.security.enums.UserRoles.ADMIN;
import static com.baravenski.musicplatform.core.security.enums.UserRoles.ARTIST;
import static com.baravenski.musicplatform.core.security.order.SecurityEndpointOrders.TRACK_UPLOAD_ORDER;

@NullMarked
@Configuration
@RequiredArgsConstructor
public class SecurityTrackUploadConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public static final String TRACKS_UPLOAD_ENDPOINTS = "api/v1/tracks/upload/**";

    @Bean
    @Order(TRACK_UPLOAD_ORDER)
    public SecurityFilterChain trackUploadFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(TRACKS_UPLOAD_ENDPOINTS)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .anyRequest().hasAnyRole(ARTIST.name(), ADMIN.name())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
package com.baravenski.musicplatform.genre.mapper;

import com.baravenski.musicplatform.genre.model.Genre;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
@Mapper(componentModel = "spring")
public interface GenreMapper {

    Genre toGenre(String name);

    @Named("mapGenresToStrings")
    default List<String> mapGenresToStrings(List<Genre> genres) {
        return genres.stream()
                .map(Genre::getName)
                .collect(Collectors.toList());
    }
}

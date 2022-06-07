package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class Genre {
    Integer id;

    @JsonProperty("name")
    @NotBlank String title;

    @JsonCreator
    public static Genre forObject(@JsonProperty("id") int id, @JsonProperty String title) {
        return new Genre(id, title);
    }
}

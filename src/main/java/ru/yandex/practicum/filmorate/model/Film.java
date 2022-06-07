package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class Film {
    Long id;

    @NotBlank
    String name;

    @NotBlank
    @Size(max = 200)
    String description;

    @Min(0)
    Integer duration;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;

    @NonNull MpaRating mpa;

    Set<Genre> genres;
}

package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.andreinc.jbvext.annotations.date.After;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
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
    long duration;

    @After(value = "1895-12-28", format = "yyyy-MM-dd") // дата не может быть раньше "дня кино".
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date releaseDate;

    Set<Long> whoLikes = new HashSet<>();

    @NonNull
    MpaRating mpa;
}

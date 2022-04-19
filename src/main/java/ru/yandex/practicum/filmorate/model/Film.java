package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import net.andreinc.jbvext.annotations.date.After;
import ru.yandex.practicum.filmorate.utils.IdProvider;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Value
public class Film {
    long id = IdProvider.getNextId(Film.class);

    @NotBlank
    String name;

    @NotBlank
    @Size(max = 200)
    String description;

    @Min(1)
    long duration;

    @After(value = "1895-12-28", format = "yyyy-MM-dd") // дата не может быть раньше "дня кино".
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date releaseDate;
}

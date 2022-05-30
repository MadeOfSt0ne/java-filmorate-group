package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class MpaRating {
    int id;

    MpaRating(@JsonProperty("id") int id) {
        this.id = id;
    }
}

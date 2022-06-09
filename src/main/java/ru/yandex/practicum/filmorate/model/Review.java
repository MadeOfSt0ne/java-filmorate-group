package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
public class Review {
    private long reviewId;
    @NotBlank
    @Size(max = 250)
    private String content;
    private boolean isPositive;
    @Positive
    private long userId;
    @Positive
    private long filmId;
    private int useful;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class Recommendation {
    Long whomId;

    Set<Long> filmsIds;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LikeReview {
    Long userId;
    Long reviewId;
    boolean isUseful;
    int useful;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeReview {
    Long userId;
    Long reviewId;
    boolean isUsefulness;
    int useful;
}

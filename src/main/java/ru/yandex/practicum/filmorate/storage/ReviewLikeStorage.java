package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.LikeReview;

public interface ReviewLikeStorage {

    void saveLike(LikeReview likeReview);

    void deleteLike(LikeReview likeReview);

    LikeReview getCountLike(Long reviewId);
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.LikeReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс-сервис для управления комментариями к фильмам.
 */
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    /**
     * Добавляет комментарий.
     *
     * @param review комментарий
     */
    public Review addNewReview(Review review) {
        return reviewStorage.add(review);
    }

    /**
     * Обновляет комментарий.
     *
     * @param review комментарий
     */
    public void updateReview(Review review) {
        reviewStorage.update(review);
    }

    /**
     * Удаляет комментарий.
     *
     * @param id комментарий
     */
    public void deleteReview(Long id) {
        reviewStorage.remove(id);
    }

    /**
     * Получает комментарий по идентификатору.
     *
     * @param id уникальный идентификатор комментария
     * @return объект комментария
     */
    public Review findReviewById(Long id) {
        Review review = reviewStorage.get(id);
        review.setUseful(reviewStorage.getCountLike(id).getUseful());
        return review;
    }

    /**
     * Получает список комментариев по идентификатору фильма.
     *
     * @param id    уникальный идентификатор комментария
     * @param count ограничение по количеству результатов
     * @return список объектов комментариев
     */
    public List<Review> getReviewsByFilmId(Long id, Integer count) {
        List<Review> list = new ArrayList<>();
        Collection<Review> collection = reviewStorage.getByFilm(id);
        for (Review review : collection) {
            review.setUseful(reviewStorage.getCountLike(review.getReviewId()).getUseful());
            list.add(review);
        }
        return list.stream().sorted(Comparator.comparing(Review::getUseful).reversed()).limit(count).collect(Collectors.toList());
    }

    /**
     * Добавляет лайк по идентификатору комментария.
     *
     * @param reviewId уникальный идентификатор комментария
     * @param userId   уникальный идентификатор пользователя
     * @param isUseful обозначение объективности комментария
     */
    public void addLike(Long reviewId, Long userId, boolean isUseful) {
        reviewStorage.saveLike(LikeReview.builder().reviewId(reviewId).userId(userId).isUsefulness(isUseful).build());
    }

    /**
     * Удаляет лайк по идентификатору комментария.
     *
     * @param reviewId уникальный идентификатор комментария
     * @param userId   уникальный идентификатор пользователя
     * @param isUseful обозначение объективности комментария
     */
    public void removeLike(Long reviewId, Long userId, boolean isUseful) {
        reviewStorage.deleteLike(LikeReview.builder().reviewId(reviewId).userId(userId).isUsefulness(isUseful).build());
    }
}

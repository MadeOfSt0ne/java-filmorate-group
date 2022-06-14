package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.LikeReview;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

/**
 * Интерфейс для хранилища комментариев к фильмам.
 */
public interface ReviewStorage {
    /**
     * Добавляет комментарий в хранилище.
     *
     * @param review фильм
     */
    Review add(Review review);

    /**
     * Возвращает комментарий.
     *
     * @param id комментарий
     */
    Review get(Long id);

    /**
     * Обновляет комментарий.
     *
     * @param review объект комментарий
     */
    void update(Review review);

    /**
     * Удаляет комментарий.
     *
     * @param id комментарий
     */
    void remove(Long id);

    /**
     * Получает список комментариев по идентификатору фильма.
     *
     * @param filmId уникальный идентификатор фильма
     * @return список объектов комментариев
     */
    Collection<Review> getByFilm(Long filmId);

    /**
     * Добавляет лайк по идентификатору комментария.
     *
     * @param likeReview объект лайка
     */
    void saveLike(LikeReview likeReview);

    /**
     * Удаляет лайк по идентификатору комментария.
     *
     * @param likeReview объект лайка
     */
    void deleteLike(LikeReview likeReview);

    /**
     * Возвращает объект с количеством лайков по комментарию.
     *
     * @param reviewId уникальный идентификатор комментария
     */
    LikeReview getCountLike(Long reviewId);
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
/**
 * Интерфейс для хранилища комментариев к фильмам.
 */
public interface ReviewStorage {
    Review add(Review review);

    Review get(Long id);

    void update(Review review);

    void remove(Long id);

    Collection<Review> getByFilm(Long filmId);
}

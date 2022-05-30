package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.IdProvider;

/**
 * Утилитарный класс-валидатор для фильмов.
 */
public class FilmValidator {
    /**
     * Валидирует объект фильма. Генерирует новый идентификатор если его нет.
     *
     * @param film валид. фильм
     * @return новый объект фильма
     */
    public static Film validate(Film film) {
        if (film.getId() == null) {
            film = film.toBuilder().id(IdProvider.getNextLongId(Film.class)).build();
        }

        return film;
    }
}

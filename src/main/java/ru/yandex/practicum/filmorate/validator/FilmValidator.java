package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
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
     * @throws ValidationException - если фильм не валиден.
     */
    public static Film validate(Film film) {
        if (film.getId() == null) {
            film = film.toBuilder().id(IdProvider.getNextLongId(Film.class)).build();
        }

        if (film.getId() < 0) throw new ValidationException("film id must be greater than zero");

        return film;
    }
}

package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.IdProvider;

import java.time.LocalDate;

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

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("release date can't be before the cinema day.");
        }

        return film;
    }
}

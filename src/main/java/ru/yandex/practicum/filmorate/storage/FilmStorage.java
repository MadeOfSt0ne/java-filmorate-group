package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

/**
 * Интерфейс для хранилища фильмов.
 */
public interface FilmStorage {
    /**
     * Получает все фильмы из хранилища.
     *
     * @return коллекция фильмов
     */
    Collection<Film> getAll();

    /**
     * Получает фильм по идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return фильм или null если фильма нет
     */
    Film get(Long id);

    /**
     * Добавляет фильм в хранилище.
     *
     * @param film фильм
     */
    void add(Film film);

    /**
     * Обновляет фильм в хранилище.
     *
     * @param film фильм
     */
    void update(Film film);

    /**
     * Удаляет фильм из хранилища.
     *
     * @param film фильм
     */
    void remove(Film film);

    /**
     * Поиск фильма по фрагменту названия
     *
     * @param str фрагмент
     */
    Collection<Film> searchFilmByTitle(String str);

    /**
     * Поиск фильма по жанру и году выпуска
     *
     * @param genreId id жанра
     * @param year    год выпуска
     * @param limit   количество отображаемых фильмов
     */
    Collection<Film> searchFilmByGenreAndYear(Integer limit, Integer genreId, Integer year);
}

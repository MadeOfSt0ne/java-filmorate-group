package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

/**
 * Интерфейс для хранилища жанров фильмов.
 */
public interface GenreStorage {
    /**
     * Получает все жанры из хранилища.
     *
     * @return коллекция жанров
     */
    Collection<Genre> getAll();

    /**
     * Получает объект жанр по уник. идентификатору.
     *
     * @param id уникальный идентификатор жанра
     * @return объект жанр
     */
    Genre get(int id);
}

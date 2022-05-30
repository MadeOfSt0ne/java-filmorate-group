package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

/**
 * Интерфейс для хранилища лайков. Предполагает совместное использование с интерфейсом FilmStorage.
 * Реализует логику хранения лайков для уже существующих в хранилище фильмов.
 */
public interface LikeStorage {

    /**
     * Возвращает список фильмов отсортированный по кол-ву лайков.
     *
     * @param limit максимальный раз списка фильмов
     * @return отсортированная коллекция фильмов
     */
    Collection<Film> getPopularFilms(Integer limit);

    /**
     * Добавляет лайк в хранилище.
     *
     * @param like лайк
     */
    void save(Like like);

    /**
     * Удаляет лайк из хранилища.
     *
     * @param like лайк
     */
    void delete(Like like);
}

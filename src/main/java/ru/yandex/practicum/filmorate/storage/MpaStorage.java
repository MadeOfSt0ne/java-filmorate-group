package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

/**
 * Интерфейс для хранилища рейтингов MPA.
 */
public interface MpaStorage {
    /**
     * Получает все рейтинги из хранилища.
     *
     * @return коллекция рейтингов
     */
    Collection<MpaRating> getAll();

    /**
     * Получает объект MPA рейтинг по идентификатору.
     *
     * @param id уникальный идентификатор рейтинга
     * @return объект рейтинг
     */
    MpaRating get(int id);
}

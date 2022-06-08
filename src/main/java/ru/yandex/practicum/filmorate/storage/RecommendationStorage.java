package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Recommendation;

/**
 * Интерфейс для хранилища рекомендаций.
 */
public interface RecommendationStorage {
    /**
     * Получает рекомендацию фильмов для пользователя.
     *
     * @param id - уникальный идентификатор пользователя
     * @return объект рекомендация для пользователя
     */
    Recommendation getRecommendationByUserId(Long id);

    /**
     * Сохраняет рекомендацию в хранилище.
     *
     * @param recommendation
     */
    void save(Recommendation recommendation);
}

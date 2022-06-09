package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Recommendation;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;

import java.util.HashSet;
import java.util.List;

/**
 * Реализация интерфейса хранилища рекомендаций с хранением в реляционной базе данных.
 */
@Service
@RequiredArgsConstructor
public class DatabaseRecommendationStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получает рекомендацию фильмов для пользователя.
     *
     * @param id - уникальный идентификатор пользователя
     * @return объект рекомендация для пользователя
     */
    @Override
    public Recommendation getRecommendationByUserId(Long id) {
        String sql = "SELECT * FROM recommendations WHERE user_id = ?";
        List<Long> filmIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"), id);
        return Recommendation.builder().whomId(id).filmsIds(new HashSet<>(filmIds)).build();
    }

    /**
     * Сохраняет рекомендацию в хранилище.
     *
     * @param recommendation
     */
    @Override
    public void save(Recommendation recommendation) {
        final String deleteSql = "DELETE FROM recommendations WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, recommendation.getWhomId());
        final String insertSql = "INSERT INTO recommendations (user_id, film_id) VALUES (?, ?)";
        recommendation.getFilmsIds().forEach(x -> jdbcTemplate.update(insertSql, recommendation.getWhomId(), x));
    }
}

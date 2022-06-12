package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Реализация интерфейса хранилища рейтингов MPA с хранением в реляционной базе данных.
 */
@Component
@RequiredArgsConstructor
public class DatabaseMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получает все рейтинги из хранилища.
     *
     * @return коллекция рейтингов
     */
    @Override
    public Collection<MpaRating> getAll() {
        final String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs));
    }

    /**
     * Получает объект MPA рейтинг по идентификатору.
     *
     * @param id уникальный идентификатор рейтинга
     * @return объект рейтинг
     */
    @Override
    public MpaRating get(int id) {
        final String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        final List<MpaRating> mpaRatings = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs), id);
        return mpaRatings.size() > 0 ? mpaRatings.get(0) : null;
    }

    private MpaRating mapRowToMpa(ResultSet rs) throws SQLException {
        return MpaRating.builder().id(rs.getInt("mpa_id")).title(rs.getString("title")).build();
    }
}

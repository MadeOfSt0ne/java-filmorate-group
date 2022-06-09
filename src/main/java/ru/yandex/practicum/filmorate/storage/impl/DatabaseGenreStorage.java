package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Реализация интерфейса хранилища жанров фильмов с хранением в реляционной базе данных.
 */
@Component
@RequiredArgsConstructor
public class DatabaseGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получает все жанры из хранилища.
     *
     * @return коллекция жанров
     */
    @Override
    public Collection<Genre> getAll() {
        final String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs));
    }

    /**
     * Получает объект жанр по уник. идентификатору.
     *
     * @param id уникальный идентификатор жанра
     * @return объект жанр
     */
    @Override
    public Genre get(int id) {
        final String sql = "SELECT * FROM genres WHERE genre_id = ?";
        final List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs), id);
        return genres.size() > 0 ? genres.get(0) : null;
    }

    private Genre mapRowToMpa(ResultSet rs) throws SQLException {
        return Genre.builder().id(rs.getInt("genre_id")).title(rs.getString("title")).build();
    }
}

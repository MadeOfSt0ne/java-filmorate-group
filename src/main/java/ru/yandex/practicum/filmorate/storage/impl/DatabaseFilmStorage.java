package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Реализация интерфейса хранилища фильмов, с хранением в реляционной базе данных.
 * Совмещена с реализацией интерфейса для хранения лайков и сортировки фильмов.
 */
@Component
@RequiredArgsConstructor
public class DatabaseFilmStorage implements FilmStorage, LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получает все фильмы из хранилища.
     *
     * @return коллекция всех фильмов
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public Collection<Film> getAll() {
        final String sql = "SELECT * FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id";
        return jdbcTemplate.query(sql, (rs, numRow) -> mapRowToFilm(rs));
    }

    /**
     * Получает фильм по идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return фильм или null если фильма нет
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public Film get(Long id) {
        final String sql = "SELECT * FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id WHERE film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, numRow) -> mapRowToFilm(rs), id);
        return films.size() > 0 ? films.get(0) : null;
    }

    /**
     * Добавляет фильм в хранилище.
     *
     * @param film фильм
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void add(Film film) {
        final String sql = "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId());
    }

    /**
     * Обновляет фильм в хранилище.
     *
     * @param film фильм
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void update(Film film) {
        final String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?"
                + " WHERE film_id = ?";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
    }

    /**
     * Удаляет фильм из хранилища.
     *
     * @param film фильм
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void remove(Film film) {
        final String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    /**
     * Возвращает список фильмов отсортированный по кол-ву лайков.
     *
     * @param limit максимальный раз списка фильмов
     * @return отсортированная коллекция фильмов
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public Collection<Film> getPopularFilms(Integer limit) {
        final String sql = "SELECT * FROM films f LEFT JOIN (SELECT film_id, COUNT(*) likes_count FROM likes"
                + " GROUP BY film_id) l ON f.film_id = l.film_id LEFT JOIN mpa ON f.mpa_id = mpa.mpa_id"
                + " ORDER BY l.likes_count DESC LIMIT ?";

        return jdbcTemplate.query(sql, (rs, numRow) -> mapRowToFilm(rs), limit);
    }

    /**
     * Добавляет лайк в хранилище.
     *
     * @param like лайк
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void save(Like like) {
        final String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, like.getUser().getId(), like.getFilm().getId());
    }

    /**
     * Удаляет лайк из хранилища.
     *
     * @param like лайк
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void delete(Like like) {
        final String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, like.getUser().getId(), like.getFilm().getId());
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(MpaRating.builder().id(rs.getInt("mpa_id")).title(rs.getString("title")).build())
                .build();
    }
}

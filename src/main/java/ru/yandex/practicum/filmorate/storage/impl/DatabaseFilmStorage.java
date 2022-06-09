package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
        final Map<Long, Set<Genre>> filmsGenres = getAllFilmsGenres();
        final String sql = "SELECT * FROM films LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id";
        return jdbcTemplate.query(sql, (rs, numRow) -> {
            final Long filmId = rs.getLong("film_id");
            return mapRowToFilm(rs, filmsGenres.get(filmId));
        });
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
        List<Film> films = jdbcTemplate.query(sql, (rs, numRow) -> mapRowToFilm(rs, getFilmGenresById(id)), id);
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

        final Set<Genre> filmGenres = film.getGenres();

        if (filmGenres != null) {
            final String genreSaveSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            filmGenres.forEach(x -> jdbcTemplate.update(genreSaveSql, film.getId(), x.getId()));
        }
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

        /*
         * TODO: ...
         */

        final String deleteGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenres, film.getId());

        final Set<Genre> filmGenres = film.getGenres();

        if (filmGenres != null) {
            final String genreSaveSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            filmGenres.forEach(x -> jdbcTemplate.update(genreSaveSql, film.getId(), x.getId()));
        }
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
     * Поиск фильма по фрагменту названия независимо от регистра.
     *
     * @param str фрагмент
     * */
    @Override
    public Collection<Film> searchFilmByTitle(String str) {
        final String slqSearch = "SELECT * FROM films AS f LEFT OUTER JOIN " +
                "(SELECT film_id, COUNT (*) likes_count FROM likes GROUP BY film_id) " +
                "AS l ON f.film_id = l.film_id LEFT OUTER JOIN mpa AS mpa ON f.mpa_id = mpa.mpa_id " +
                "WHERE f.name ILIKE CONCAT('%', ?, '%')" +
                "ORDER BY l.likes_count DESC;";

        final Map<Long, Set<Genre>> filmsGenres = getAllFilmsGenres();

        return jdbcTemplate.query(slqSearch, (rs, rowNum) -> {
            final Long filmId = rs.getLong("film_id");
            return mapRowToFilm(rs, filmsGenres.get(filmId));
        }, str);
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

        final Map<Long, Set<Genre>> filmsGenres = getAllFilmsGenres();

        return jdbcTemplate.query(sql, (rs, numRow) -> {
            final Long filmId = rs.getLong("film_id");
            return mapRowToFilm(rs, filmsGenres.get(filmId));
        }, limit);
    }

    @Override
    public Collection<Film> getPopularFilmByUserId(Long id) {
        final String sql = "SELECT * FROM films " +
                "LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id " +
                "LEFT JOIN (SELECT film_id, user_id, COUNT(*) likes_count FROM likes GROUP BY user_id, film_id)" +
                " l ON films.film_id = l.film_id " +
                "WHERE l.user_id = ? " +
                "ORDER BY l.likes_count DESC;";

        final Map<Long, Set<Genre>> filmsGenres = getAllFilmsGenres();

        return jdbcTemplate.query(sql, (rs, numRow) -> {
            final Long filmId = rs.getLong("film_id");
            return mapRowToFilm(rs, filmsGenres.get(filmId));
        }, id);
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

    private Map<Long, Set<Genre>> getAllFilmsGenres() {
        final String sql = "SELECT * FROM film_genres INNER JOIN genres ON genres.genre_id = film_genres.genre_id";

        final Map<Long, Set<Genre>> filmsGenres = new HashMap<>();

        jdbcTemplate.query(sql, (RowCallbackHandler) rs -> {
            final Long filmId = rs.getLong("film_id");
            filmsGenres.getOrDefault(filmId, new HashSet<>()).add(Genre.builder().id(rs.getInt("genre_id"))
                    .title(rs.getString("title")).build());
        });

        return filmsGenres;
    }

    private Set<Genre> getFilmGenresById(Long id) {
        final String sql = "SELECT * FROM film_genres INNER JOIN genres ON genres.genre_id = film_genres.genre_id"
                + " WHERE film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, (rs, getNum) -> Genre.builder().id(rs.getInt("genre_id"))
                .title(rs.getString("title")).build(), id));
    }

    private Film mapRowToFilm(ResultSet rs, Set<Genre> genres) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .genres(genres != null && genres.isEmpty() ? null : genres)
                .mpa(MpaRating.builder().id(rs.getInt("mpa_id")).title(rs.getString("title")).build())
                .build();
    }
}

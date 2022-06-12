package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

/**
 * Реализация интерфейса хранилища фильмов с хранением в реляционной базе данных.
 * Совмещена с реализацией интерфейса для хранения лайков и сортировки фильмов.
 */
@Component
@RequiredArgsConstructor
public class DatabaseFilmStorage implements FilmStorage, LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_SEARCH_TITLE = "SELECT * FROM films AS f LEFT OUTER JOIN " +
            "(SELECT film_id, COUNT (*) likes_count FROM likes GROUP BY film_id) " +
            "AS l ON f.film_id = l.film_id " +
            "LEFT OUTER JOIN mpa AS mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE f.name ILIKE CONCAT('%', ?, '%')" +
            "ORDER BY l.likes_count DESC;";

    private static final String SQL_SEARCH_GENRE_YEAR = "SELECT * FROM films AS f " +
            "LEFT OUTER JOIN (SELECT film_id, COUNT (*) likes_count FROM likes GROUP BY film_id) " +
            "AS l ON f.film_id = l.film_id " +
            "LEFT OUTER JOIN mpa AS mpa ON f.mpa_id = mpa.mpa_id " +
            "LEFT OUTER JOIN film_genres AS fg ON f.film_id = fg.film_id " +
            "WHERE fg.genre_id = ? AND EXTRACT (YEAR FROM f.release_date) = ? " +
            "ORDER BY l.likes_count DESC " +
            "LIMIT ?;";

    private static final String SQL_SEARCH_GENRE = "SELECT * FROM films AS f " +
            "LEFT OUTER JOIN (SELECT film_id, COUNT (*) likes_count FROM likes GROUP BY film_id) " +
            "AS l ON f.film_id = l.film_id " +
            "LEFT OUTER JOIN mpa AS mpa ON f.mpa_id = mpa.mpa_id " +
            "LEFT OUTER JOIN film_genres AS fg ON f.film_id = fg.film_id " +
            "WHERE fg.genre_id = ? " +
            "ORDER BY l.likes_count DESC " +
            "LIMIT ?;";

    private static final String SQL_SEARCH_YEAR = "SELECT * FROM films AS f " +
            "LEFT OUTER JOIN (SELECT film_id, COUNT (*) likes_count FROM likes GROUP BY film_id) " +
            "AS l ON f.film_id = l.film_id " +
            "LEFT OUTER JOIN mpa AS mpa ON f.mpa_id = mpa.mpa_id " +
            "WHERE EXTRACT (YEAR FROM f.release_date) = ? " +
            "ORDER BY l.likes_count DESC " +
            "LIMIT ?;";

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
     */
    @Override
    public Collection<Film> searchFilmByTitle(String str) {

        return jdbcTemplate.query(SQL_SEARCH_TITLE, (rs, rowNum) -> {
            final Long filmId = rs.getLong("film_id");
            return mapRowToFilm(rs, getFilmGenresById(filmId));
        }, str);

    }

    /**
     * Поиск фильма по жанру и/или году выпуска.
     *
     * @param genreId id жанра
     * @param year    год выпуска
     * @param limit   количество отображаемых фильмов
     */
    @Override
    public Collection<Film> searchFilmByGenreAndYear(Integer limit, Integer genreId, Integer year) {
        if (year != 5 && genreId != 555) {
            return jdbcTemplate.query(SQL_SEARCH_GENRE_YEAR, (rs, rowNum) -> {
                final Long filmId = rs.getLong("film_id");
                return mapRowToFilm(rs, getFilmGenresById(filmId));
            }, genreId, year, limit);
        }
        if (year == 5 && genreId != 555) {
            return jdbcTemplate.query(SQL_SEARCH_GENRE, (rs, rowNum) -> {
                final Long filmId = rs.getLong("film_id");
                return mapRowToFilm(rs, getFilmGenresById(filmId));
            }, genreId, limit);
        }
        if (year != 5) {
            return jdbcTemplate.query(SQL_SEARCH_YEAR, (rs, rowNum) -> {
                final Long filmId = rs.getLong("film_id");
                return mapRowToFilm(rs, getFilmGenresById(filmId));
            }, year, limit);
        }
        throw new NoSuchElementException();

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
     * Возвращает лайки всех пользователей сгруппированные по идентификатору пользователя.
     *
     * @return таблица лайков
     */
    @Override
    public Map<Long, Set<Long>> getUsersLikesMap() {
        final String sql = "SELECT * FROM likes";

        final Map<Long, Set<Long>> usersLikes = new HashMap<>();

        jdbcTemplate.query(sql, (rs) -> {
            final Long userId = rs.getLong("user_id");
            final Long filmId = rs.getLong("film_id");
            usersLikes.merge(userId, new HashSet<>(Set.of(filmId)), (oldVal, newVal) -> {
                oldVal.add(filmId);
                return oldVal;
            });
        });

        return usersLikes;
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
        addEvent(like, EventType.LIKE, EventType.ADD);
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
        addEvent(like, EventType.LIKE, EventType.REMOVE);
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

    private void addEvent(Like like, EventType eventType, EventType eventOperation) {
        jdbcTemplate.update("INSERT INTO events (USER_ID, EVENT_TYPE, OPERATION, TIME_STAMP, ENTITY_ID) " +
                        "VALUES (?, ?, ?, ?, ?)",
                like.getUser().getId(),
                eventType.toString(),
                eventOperation.toString(),
                Instant.now().toEpochMilli(),
                like.getFilm().getId());
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Класс-сервис для управления фильмами.
 */
@Service
@RequiredArgsConstructor
public class FilmService {
    private final Comparator<Film> filmLikesComparator = Comparator.comparingInt(film -> film.getWhoLikes().size());
    private final FilmStorage filmStorage;
    private final UserService userService;

    /**
     * Получает все фильмы.
     *
     * @return коллекция фильмов
     */
    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    /**
     * Получает фильм по идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return объект фильма
     * @throws NoSuchElementException - если фильма не существует.
     */
    public Film getFilm(final Long id) {
        final Film film = filmStorage.get(id);
        if (film == null) throw new NoSuchElementException();
        return film;
    }

    /**
     * Добавляет фильм.
     *
     * @param newFilm фильм
     */
    public void addFilm(final Film newFilm) {
        filmStorage.add(newFilm);
    }

    /**
     * Обновляет фильм.
     *
     * @param updatedFilm фильм
     * @throws NoSuchElementException - если фильма не существует.
     */
    public void updateFilm(final Film updatedFilm) {
        final Film film = getFilm(updatedFilm.getId());
        if (updatedFilm.equals(film)) return;
        filmStorage.update(updatedFilm);
    }

    /**
     * Удаляет фильм по идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @throws NoSuchElementException - если фильма не существует.
     */
    public void removeFilm(final Long id) {
        filmStorage.remove(getFilm(id));
    }

    /**
     * Добавляет лайк пользователя к фильму.
     *
     * @param id     уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     * @throws NoSuchElementException - если фильма не существует.
     */
    public void addLikeToFilm(final Long id, final Long userId) {
        getFilm(id).getWhoLikes().add(userService.getUser(userId).getId());
    }

    /**
     * Удаляет лайк пользователя с фильма.
     *
     * @param id     уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     * @throws NoSuchElementException - если фильма не существует.
     */
    public void removeLikeFromFilm(final Long id, final Long userId) {
        getFilm(id).getWhoLikes().remove(userService.getUser(userId).getId());
    }

    /**
     * Получает список самых популярных (залайканных) фильмов.
     *
     * @param count кол-во фильмов
     * @return коллекция из фильмов
     * @throws NoSuchElementException - если фильма не существует.
     */
    public Collection<Film> getPopularFilms(final Long count) {
        return filmStorage.getAll().stream().sorted(filmLikesComparator.reversed()).limit(count == null ? 10 : count)
                .collect(Collectors.toList());
    }
}

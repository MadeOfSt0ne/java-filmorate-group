package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.events.FilmLikeAddedEvent;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;

/**
 * Класс-сервис для управления фильмами.
 */
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;
    private final ApplicationEventPublisher publisher;

    @Autowired
    FilmService(FilmStorage databaseFilmStorage,
                @Qualifier("databaseFilmStorage") LikeStorage databaseLikeStorage,
                UserService userService,
                ApplicationEventPublisher publisher
    ) {
        this.filmStorage = databaseFilmStorage;
        this.userService = userService;
        this.likeStorage = databaseLikeStorage;
        this.publisher = publisher;
    }

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
        likeStorage.save(Like.builder().film(getFilm(id)).user(userService.getUser(userId)).build());
        publisher.publishEvent(new FilmLikeAddedEvent(this));
    }

    /**
     * Удаляет лайк пользователя с фильма.
     *
     * @param id     уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     * @throws NoSuchElementException - если фильма не существует.
     */
    public void removeLikeFromFilm(final Long id, final Long userId) {
        likeStorage.delete(Like.builder().film(getFilm(id)).user(userService.getUser(userId)).build());
    }

    /**
     * Получает список самых популярных (залайканных) фильмов.
     *
     * @param count кол-во фильмов
     * @return коллекция из фильмов
     * @throws NoSuchElementException - если фильма не существует.
     */
    public Collection<Film> getPopularFilms(final Integer count) {
        return likeStorage.getPopularFilms(count != null ? count : 10);
    }

    /**
     * Поиск фильма по фрагменту названия независимо от регистра.
     *
     * @param substring фрагмент
     */
    public Collection<Film> searchFilmByTitle(final String substring, final String title) {
        if (substring == null || !title.equals("title")) {
            return null;
        }
        return filmStorage.searchFilmByTitle(substring);
    }

    public Collection<Film> getCommonPopular(final Long userId, final Long friendId) {
        if (userService.getUser(userId) == null || userService.getUser(friendId) == null) {
            throw new NoSuchElementException();
        }
        if (userService.getUserFriends(userId).contains(userService.getUser(friendId)) ||
                userService.getUserFriends(friendId).contains(userService.getUser(userId))) {

            Set<Film> intersection = new HashSet<>(likeStorage.getPopularFilmByUserId(friendId));
            intersection.retainAll(likeStorage.getPopularFilmByUserId(userId));
            return intersection;
        } else
            return new ArrayList<>();
    }

    /**
     * Поиск фильма по жанру и году выпуска
     *
     * @param genreId id жанра
     * @param year    год выпуска
     * @param limit   количество отображаемых фильмов
     */
    public Collection<Film> searchFilmByGenreAndYear(int limit, int genreId, int year) {
        if (limit <= 0 || year < 1895 || genreId <=0) {
            throw new ValidationException("Данные запроса не корректны.");
        }
        return filmStorage.searchFilmByGenreAndYear(limit, genreId, year);
    }

}

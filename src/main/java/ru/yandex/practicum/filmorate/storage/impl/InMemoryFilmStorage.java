package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса хранилища фильмов, с хранением в оперативной памяти.
 */
@Component
public class InMemoryFilmStorage implements FilmStorage, LikeStorage {
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final HashMap<Long, Film> films = new HashMap<>();

    /**
     * Получает все фильмы из хранилища.
     *
     * @return коллекция фильмов
     */
    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    /**
     * Получает фильм по идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return об. фильм или null если фильма нет
     */
    @Override
    public Film get(Long id) {
        return films.get(id);
    }

    /**
     * Добавляет фильм в хранилище.
     *
     * @param film фильм
     */
    @Override
    public void add(Film film) {
        films.put(film.getId(), film);
    }

    /**
     * Обновляет фильм в хранилище. По факту просто заменяет старый на новый.
     *
     * @param film фильм
     */
    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    /**
     * Удаляет фильм из хранилища.
     *
     * @param film фильм
     */
    @Override
    public void remove(Film film) {
        films.remove(film.getId());
    }

    /**
     * Реализация метода поиска фильма в inMemory не требуется, поэтому метод возвращает ошибку.
     *
     * @param str фрагмент названия
     */
    @Override
    public Collection<Film> searchFilmByTitle(String str) {
        throw new UnsupportedOperationException();
    }

    /**
     * Возвращает список фильмов отсортированный по кол-ву лайков.
     *
     * @param limit максимальный раз списка фильмов
     * @return отсортированная коллекция фильмов
     */
    @Override
    public Collection<Film> getPopularFilms(Integer limit) {
        final Comparator<Film> comparator = Comparator.comparingInt(x -> likes.getOrDefault(x.getId(),
                new HashSet<>()).size());

        return films.values().stream().sorted(comparator.reversed()).limit(limit).collect(Collectors.toList());
    }

    /**
     * Возвращает фильмы, которые лайкнул пользователь
     *
     * @param id id пользователя
     */
    @Override
    public Collection<Film> getPopularFilmByUserId(Long id) {
        HashMap<Long, Set<Long>> whoLikes = new HashMap<>(likes);
        whoLikes.values().removeIf(userSet -> !userSet.contains(id));

        return whoLikes.keySet().stream().map(films::get).collect(Collectors.toList());
    }

    /**
     * Добавляет лайк в хранилище.
     *
     * @param like лайк
     */
    @Override
    public void save(Like like) {
        Set<Long> whoLikes = likes.getOrDefault(like.getFilm().getId(), new HashSet<>());
        whoLikes.add(like.getUser().getId());
        likes.put(like.getFilm().getId(), whoLikes);
    }

    /**
     * Удаляет лайк из хранилища.
     *
     * @param like лайк
     */
    @Override
    public void delete(Like like) {
        likes.get(like.getFilm().getId()).remove(like.getUser().getId());
    }
}

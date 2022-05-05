package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

/**
 * Реализация интерфейса хранилища фильмов, с хранением в оперативной памяти.
 */
@Component
public class InMemoryFilmStorage implements FilmStorage {
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
}

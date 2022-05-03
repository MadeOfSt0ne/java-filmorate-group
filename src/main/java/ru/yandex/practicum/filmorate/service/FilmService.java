package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        if (film == null || user == null) throw new NoSuchElementException();

        film.getWhoLikes().add(user.getId());
    }

    public void removeLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        if (film == null || user == null) throw new NoSuchElementException();

        film.getWhoLikes().add(user.getId());
    }

    public Collection<Film> getPopular(Long query) {
        return filmStorage.getAll().stream().sorted().collect(Collectors.toList());
    }
}

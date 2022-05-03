package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * REST-контроллер для фильмов.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage storage;
    private final FilmService service;

    @GetMapping
    Collection<Film> getAll() {
        return storage.getAll();
    }

    @GetMapping("{id}")
    Film get(@PathVariable Long id) {
        Film film = storage.get(id);
        if (film == null) throw new NoSuchElementException();
        return storage.get(id);
    }

    @PostMapping
    Film create(@Valid @RequestBody Film film) {
        film = FilmValidator.validate(film);
        log.info("CREATE {}", film);
        storage.add(film);
        return film;
    }

    @PutMapping
    Film update(@Valid @RequestBody Film film) {
        film = FilmValidator.validate(film);
        log.info("UPDATE {}", film);
        storage.update(film);
        return film;
    }

    @GetMapping("/popular")
    Collection<Film> getPopular(@RequestParam(required = false) Long query) {
        return service.getPopular(query);
    }

    @PutMapping("{id}/like/{userId}")
    void addLike(@PathVariable Long id, @PathVariable Long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        service.removeLike(id, userId);
    }
}

package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

/**
 * REST-контроллер для Film'ов.
 *
 * Для валидации объектов целиком полагается на спецификацию JSR-303.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Long, Film> films = new HashMap<>();

    @GetMapping
    Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("CREATE {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("UPDATE {}", film);

        if (!films.containsKey(film.getId())) {
            log.warn("Can't find obj with id={}", film.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return films.put(film.getId(), film);
    }

    /**
     * Сбрасывает внут. состояние, очищает хранилище.
     * Используется только при интегр. тестировании.
     */
    public void clear() {
        films.clear();
    }
}

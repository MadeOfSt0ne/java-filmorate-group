package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.Collection;

/**
 * REST-контроллер для фильмов.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    Collection<Film> getAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")
    Film get(@PathVariable final Long id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    Film create(@Valid @RequestBody final Film film) {
        final Film validatedFilm = FilmValidator.validate(film);
        log.info("CREATE {}", validatedFilm);
        filmService.addFilm(validatedFilm);
        return validatedFilm;
    }

    @PutMapping
    Film update(@Valid @RequestBody final Film film) {
        final Film validatedFilm = FilmValidator.validate(film);
        log.info("UPDATE {}", validatedFilm);
        filmService.updateFilm(validatedFilm);
        return validatedFilm;
    }

    @GetMapping("/popular")
    Collection<Film> getPopular(@RequestParam(required = false) final Integer count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping("{id}/like/{userId}")
    void addLike(@PathVariable final Long id, @PathVariable final Long userId) {
        log.info("USER ({}) LIKES FILM ({})", userId, id);
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    void removeLike(@PathVariable final Long id, @PathVariable final Long userId) {
        log.info("USER ({}) DON'T LIKE FILM ({})", userId, id);
        filmService.removeLikeFromFilm(id, userId);
    }

    @GetMapping("/search?query={substring}&by={title}")
    Collection<Film> searchFilm(@PathVariable final String substring, @PathVariable final String title) {
        return filmService.searchFilm(substring, title);
    }
}

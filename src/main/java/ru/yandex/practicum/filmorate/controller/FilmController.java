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
import java.util.OptionalInt;

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

    @DeleteMapping("{id}")
    void delete(@PathVariable final Long id) {
        filmService.removeFilm(id);
    }

    @GetMapping("/popular")
    Collection<Film> getPopular(@RequestParam(value = "count", defaultValue = "10") final Integer count,
                                @RequestParam(value = "genreId", required = false) Integer genreId,
                                @RequestParam(value = "year", required = false) Integer year) {
        //if (genreId == null && year == null) return filmService.getPopularFilms(count);
        return filmService.searchFilmByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/request?userId={userId}&friendId={friendId}")
    Collection<Film> getCommonPopularFilms(@PathVariable final Long userId, @PathVariable final Long friendId) {
        return filmService.getCommonPopular(userId, friendId);
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

    @GetMapping("/search")
    Collection<Film> searchFilmByTitle(@RequestParam final String substring, @RequestParam final String title) {
        return filmService.searchFilmByTitle(substring, title);
    }
}

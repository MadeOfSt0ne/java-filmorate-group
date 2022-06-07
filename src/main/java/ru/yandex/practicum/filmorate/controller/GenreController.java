package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreStorage genreStorage;

    @GetMapping
    Collection<Genre> getAll() {
        return genreStorage.getAll();
    }

    @GetMapping("{id}")
    Genre get(@PathVariable final int id) {
        Genre genre = genreStorage.get(id);
        if (genre == null) throw new NoSuchElementException();
        return genre;
    }
}

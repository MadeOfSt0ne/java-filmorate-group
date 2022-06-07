package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaStorage mpaStorage;

    @GetMapping
    Collection<MpaRating> getAll() {
        return mpaStorage.getAll();
    }

    @GetMapping("{id}")
    MpaRating get(@PathVariable final int id) {
        MpaRating mpaRating = mpaStorage.get(id);
        if (mpaRating == null) throw new NoSuchElementException();
        return mpaRating;
    }
}

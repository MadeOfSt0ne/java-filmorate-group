package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    @Autowired
    private FilmStorage filmStorage;

    private final Film film = Film.builder().name("test").description("test").releaseDate(new Date(0L)).build();

    private final User user = User.builder().login("xx").email("xxxxx@xxxxx.ru").birthday(new Date(0L)).build();

    @AfterEach
    void tearDown() {
        filmStorage.clear();
    }

    @Test
    void contextLoads() {
        assertNotNull(filmController);
    }

    @Test
    void testGetAllWithoutFilms() {
        assertEquals(Collections.EMPTY_LIST, new ArrayList<>(filmController.getAll()));
    }

    @Test
    void testGetAllWithFilms() {
        final Film film1 = filmController.create(film);
        assertEquals(List.of(film1), new ArrayList<>(filmController.getAll()));
    }

    @Test
    void testGetNonExistFilm() {
        final Film film1 = filmController.create(film);
        filmStorage.remove(film1);
        assertThrows(NoSuchElementException.class, () -> filmController.get(1L));
    }

    @Test
    void testCreateCorrectFilm() {
        final Film film1 = filmController.create(film);
        assertEquals(film1, filmController.get(film1.getId()));
    }

    @Test
    void testCreateFilmWithNegativeId() {
        assertThrows(ValidationException.class, () -> filmController.create(film.toBuilder().id(-1L).build()));
    }

    @Test
    void testCreateFilmWithIncorrectDate() {
        assertThrows(ConstraintViolationException.class, () -> filmController.create(film.toBuilder()
                .releaseDate(Date.from(Instant.parse("1700-01-01T00:00:00.00Z"))).build()));
    }

    @Test
    void testCreateFilmWithIncorrectDuration() {
        assertThrows(ConstraintViolationException.class,
                () -> filmController.create(film.toBuilder().duration(-1).build()));
    }

    @Test
    void testCreateFilmWithTooLongDescription() {
        final String description = "adipisicingadipisicingadipisicingadipisicingadipisicingadipisicinadipisicingg" +
                "adipisicingadipisicingadipisicingadipisicingadipisicingadipisicingadipisicingadipisadipisdicingh" +
                "adipisicingadipisicingadipisicingadipisicingadipisicingadipisicingwerwrwerfsfsdfsdfsdfsdfsdfsdfs";

        assertThrows(ConstraintViolationException.class,
                () -> filmController.create(film.toBuilder().description(description).build()));
    }

    @Test
    void testCreateFilmWithoutName() {
        assertThrows(ConstraintViolationException.class,
                () -> filmController.create(film.toBuilder().name("").build()));
    }

    @Test
    void testUpdateFilm() {
        final Film film1 = filmController.create(film);
        final Film film2 = film1.toBuilder().duration(100).build();
        filmController.update(film2);
        assertEquals(film2, filmController.get(film2.getId()));
    }

    @Test
    void testUpdateNonExistedFilm() {
        assertThrows(NoSuchElementException.class, () -> filmController.update(film));
    }

    @Test
    void testAddLikeToFilm() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        filmController.addLike(film1.getId(), user1.getId());
        assertEquals(List.of(user1.getId()), new ArrayList<>(filmController.get(film1.getId()).getWhoLikes()));
    }

    @Test
    void testAddLikeToNonExistFilm() {
        assertThrows(NoSuchElementException.class, () -> filmController.addLike(0L, 0L));
    }

    @Test
    void testRemoveLikeFromFilm() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        filmController.addLike(film1.getId(), user1.getId());
        filmController.removeLike(film1.getId(), user1.getId());
        assertEquals(Collections.EMPTY_LIST, new ArrayList<>(filmController.get(film1.getId()).getWhoLikes()));
    }

    @Test
    void testGetPopularFilms() {
        final Film film1 = filmController.create(film);
        final Film film2 = filmController.create(film.toBuilder().name("test2").build());
        final User user1 = userController.create(user);
        filmController.addLike(film2.getId(), user1.getId());
        assertEquals(List.of(film2), filmController.getPopular(1L));
    }
}
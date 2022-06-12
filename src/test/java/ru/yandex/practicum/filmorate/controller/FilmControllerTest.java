package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    @Autowired
    @Qualifier("databaseFilmStorage")
    private FilmStorage filmStorage;

    private final Film film = Film.builder().name("test").description("test").mpa(MpaRating.builder().id(1)
            .title("G").build()).duration(0).releaseDate(LocalDate.of(1970, 1, 1)).build();

    private final User user = User.builder().login("xx").email("xxxxx@xxxxx.ru")
            .birthday(LocalDate.of(1970, 1, 1)).build();

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
    void testCreateFilmWithIncorrectDate() {
        assertThrows(ValidationException.class, () -> filmController.create(film.toBuilder()
                .releaseDate(LocalDate.of(1700, 1, 1)).build()));
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
        final Film film2 = filmController.create(film);
        final User user1 = userController.create(user);
        filmController.addLike(film2.getId(), user1.getId());
        assertEquals(film2, filmController.getPopular(1).stream().findFirst().orElse(null));
    }

    @Test
    void testAddLikeToNonExistFilm() {
        assertThrows(NoSuchElementException.class, () -> filmController.addLike(0L, 0L));
    }

    @Test
    void testRemoveLikeFromFilm() {
        final Film film1 = filmController.create(film);
        final Film film2 = filmController.create(film);
        final User user1 = userController.create(user);
        filmController.addLike(film1.getId(), user1.getId());
        filmController.removeLike(film1.getId(), user1.getId());
        assertEquals(film1, filmController.getPopular(1).stream().findFirst().orElse(null));
    }

    @Test
    void testGetPopularFilms() {
        final Film film1 = filmController.create(film);
        final Film film2 = filmController.create(film.toBuilder().name("test2").build());
        final User user1 = userController.create(user);
        filmController.addLike(film2.getId(), user1.getId());
        assertEquals(List.of(film2), filmController.getPopular(1));
    }

    @Test
    void testSearchFilmByFragmentOfName() {
        final Film film1 = filmController.create(film);
        final Film film2 = filmController.create(film.toBuilder().name("Terminator").build());
        final User user1 = userController.create(user);
        filmController.addLike(film2.getId(), user1.getId());
        assertEquals(List.of(film2), filmController.searchFilmByTitle("tERm", "title"));
    }

    @Test
    void testSearchFilmByGenreAndYear() {
        final Genre genre = Genre.builder().id(1).title("Comedy").build();
        final Film film1 = filmController.create(film.toBuilder().genres(Set.of(genre)).build());
        final User user1 = userController.create(user);
        filmController.addLike(film1.getId(), user1.getId());
        assertEquals(List.of(film1), filmController.getPopular(7, 1, 1970));
    }

    @Test
    void testGetCommonPopularFilms() {
        final Film film1 = filmController.create(film);
        final Film film2 = filmController.create(film
                .toBuilder()
                .name("test2")
                .build());
        final User user1 = userController.create(user);

        final User user2 = User
                .builder()
                .login("testLogin")
                .email("test@test.ru")
                .birthday(LocalDate.of(1970, 1, 1))
                .build();
        final User user3 = userController.create(user2);

        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film1.getId(), user3.getId());
        filmController.addLike(film2.getId(), user3.getId());
        assertEquals(0, filmController.getCommonPopularFilms(user1.getId(), user3.getId()).size());
        //Не друзья, нет общих фильмов
        userController.addFriends(user1.getId(), user3.getId());
        userController.addFriends(user3.getId(), user1.getId());
        assertEquals(1, filmController.getCommonPopularFilms(user1.getId(), user3.getId()).size());
        //Друзья, есть общий фильм
    }
}

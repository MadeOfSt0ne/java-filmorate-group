package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    @Autowired
    private ReviewController reviewController;

    @Autowired
    @Qualifier("databaseFilmStorage")
    private FilmStorage filmStorage;

    private final Film film = Film.builder().name("test").description("test").mpa(MpaRating.builder().id(1)
            .title("G").build()).duration(0).releaseDate(LocalDate.of(1970, 1, 1)).build();

    private final User user = User.builder().login("xx").email("xxxxx@xxxxx.ru")
            .birthday(LocalDate.of(1970, 1, 1)).build();
    private final Review review = Review.builder().filmId(1).userId(1).isPositive(true).content("wwwwww").build();

    @Test
    void contextLoads() {
        assertNotNull(reviewController);
    }

    @Test
    void createCorrectReview() {
        filmController.create(film);
        userController.create(user);
        Review review1 = reviewController.create(review);
        assertEquals(review1, reviewController.get(review1.getReviewId()));
    }
}
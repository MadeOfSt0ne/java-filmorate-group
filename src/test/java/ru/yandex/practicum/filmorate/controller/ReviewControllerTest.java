package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testCreateCorrectReview() {
        Film film1 = filmController.create(film);
        User user1 = userController.create(user);
        Review review1 = reviewController.create(Review.builder().filmId(film1.getId()).userId(user1.getId()).isPositive(true).content("wwwwww").build());
        assertEquals(review1, reviewController.get(review1.getReviewId()));
    }

    @Test
    void testAddLikeToReview() {
        Film film1 = filmController.create(film);
        User user1 = userController.create(user);
        Review review1 = reviewController.create(Review.builder().filmId(film1.getId()).userId(user1.getId()).isPositive(true).content("wwwwww").build());
        reviewController.addLike(review1.getReviewId(), user1.getId());
        review1 = reviewController.get(1L);
        assertEquals(review1, reviewController.getAllByFilm(film1.getId(), 10).stream().findFirst().orElse(null));
    }

    @Test
    void testRemoveLikeToReview() {
        Film film1 = filmController.create(film);
        User user1 = userController.create(user);
        Review review1 = reviewController.create(Review.builder().filmId(film1.getId()).userId(user1.getId()).isPositive(true).content("wwwwww").build());
        reviewController.addLike(review1.getReviewId(), user1.getId());
        reviewController.removeLike(review1.getReviewId(), user1.getId());
        assertEquals(review1, reviewController.getAllByFilm(film1.getId(), 10).stream().findFirst().orElse(null));
    }

    @Test
    void testGetPopularReview() {
        Film film1 = filmController.create(film);
        User user1 = userController.create(user);
        Review review1 = reviewController.create(Review.builder().filmId(film1.getId()).userId(user1.getId()).isPositive(true).content("wwwwww").build());
        Review review2 = reviewController.create(Review.builder().filmId(film1.getId()).userId(user1.getId()).isPositive(false).content("222").build());
        reviewController.addLike(review1.getReviewId(), user1.getId());
        reviewController.addDisLike(review2.getReviewId(), user1.getId());
        review1 = reviewController.get(1L);
        assertEquals(review1, reviewController.getAllByFilm(film1.getId(), 1).stream().findFirst().orElse(null));
    }

}

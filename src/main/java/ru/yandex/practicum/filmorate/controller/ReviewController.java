package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

/**
 * REST-контроллер для отзывов к фильмам.
 */
@Slf4j
@RestController
@Validated
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    Review create(@Valid @RequestBody Review review) {
        log.info("CREATE {}", review);
        return reviewService.addNewReview(review);
    }

    @PutMapping
    void update(@Valid @RequestBody Review review) {
        log.info("UPDATE {}", review);
        reviewService.updateReview(review);
    }

    @GetMapping("{id}")
    Review get(@PathVariable Long id) {
        log.info("GET BY ID {}", id);
        return reviewService.findReviewById(id);
    }
    @DeleteMapping("{id}")
    void delete(@PathVariable Long id) {
        log.info("DELETE BY ID {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping
    List<Review> getAllByFilm(@RequestParam Long filmId, @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("GET ALL BY FILM ID {} COUNT {}", filmId, count);
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("USER ({}) ADDS LIKE TO REVIEW ({}) ", userId, id);
        reviewService.addLike(id, userId, true);
    }

    @PutMapping("{id}/dislike/{userId}")
    void addDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("USER ({}) ADDS DISLIKE TO REVIEW ({}) ", userId, id);
        reviewService.addLike(id, userId, false);
    }

    @DeleteMapping("{id}/like/{userId}")
    void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("USER ({}) DELETE LIKE TO REVIEW ({}) ", userId, id);
        reviewService.removeLike(id, userId, true);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    void removeDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("USER ({}) DELETE DISLIKE TO REVIEW ({}) ", userId, id);
        reviewService.removeLike(id, userId, false);
    }
}

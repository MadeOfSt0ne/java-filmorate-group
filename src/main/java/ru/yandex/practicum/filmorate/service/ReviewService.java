package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.LikeReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    private final ReviewLikeStorage reviewLikeStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, ReviewLikeStorage reviewLikeStorage) {
        this.reviewStorage = reviewStorage;
        this.reviewLikeStorage = reviewLikeStorage;
    }

    public Review addNewReview(Review review) {
        return reviewStorage.add(review);
    }

    public void updateReview(Review review) {
        reviewStorage.update(review);
    }

    public Review findReviewById(Long id) {
        Review review = reviewStorage.get(id);
        review.setUseful(reviewLikeStorage.getCountLike(id).getUseful());
        return review;
    }

    public List<Review> getReviewsByFilmId(Long id) {
        List<Review> list = new ArrayList<>();
        Collection<Review> collection = reviewStorage.getByFilm(id);
        for (Review review : collection) {
            review.setUseful(reviewLikeStorage.getCountLike(id).getUseful());
            list.add(review);
        }
        list.stream().sorted(Comparator.comparing(Review::getUseful).reversed()).collect(Collectors.toList());
        return list;
    }

    public void addLike(Long reviewId, Long userId, boolean isUseful) {
        reviewLikeStorage.saveLike(LikeReview.builder().reviewId(reviewId).userId(userId).isUseful(isUseful).build());
    }

    public void removeLike(Long reviewId, Long userId, boolean isUseful) {
        reviewLikeStorage.saveLike(LikeReview.builder().reviewId(reviewId).userId(userId).isUseful(isUseful).build());
    }
}

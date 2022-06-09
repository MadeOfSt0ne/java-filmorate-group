package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LikeReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class DatabaseReviewStorage implements ReviewStorage, ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseReviewStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review add(Review review) {
        String query = "INSERT INTO review ( content, is_positive, user_id, film_id) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.isPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        return review;
    }

    @Override
    public Review get(Long id) {
        String query = "SELECT review_id, content, is_positive, user_id, film_id FROM review WHERE review_id=?";
        return jdbcTemplate.queryForObject(query, this::mapRowToReview, id);
    }

    @Override
    public void update(Review review) {
        String query = "UPDATE review SET content=?, is_positive=?, user_id=?, film_id=? WHERE review_id=?";
        jdbcTemplate.update(query,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM review WHERE review_id=?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public Collection<Review> getByFilm(Long filmId) {
        String query = "SELECT review_id, content, is_positive, user_id, film_id FROM review WHERE film_id=?";
        return jdbcTemplate.query(query, this::mapRowToReview, filmId);
    }

    @Override
    public void saveLike(LikeReview likeReview) {
        String query = "INSERT INTO review_like (user_id, review_id, is_useful) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, likeReview.getUserId(), likeReview.getReviewId(), likeReview.isUseful());
    }

    @Override
    public void deleteLike(LikeReview likeReview) {
        String query = "DELETE FROM review_like WHERE user_id = ? AND review_id = ?";
        jdbcTemplate.update(query, likeReview.getUserId(), likeReview.getReviewId());

    }

    @Override
    public LikeReview getCountLike(Long reviewId) {
        String query = "SELECT t1.review_id, (t1.count-t2.count) AS useful " +
                "FROM (SELECT COUNT(review_id) as count, review_id FROM review_like WHERE is_useful=TRUE GROUP BY review_id) " +
                "AS t1  LEFT JOIN(SELECT COUNT(review_id) as count, review_id FROM review_like WHERE is_useful=FALSE GROUP BY review_id) " +
                "AS t2 ON(t1.review_id=t2.review_id) " +
                "WHERE t1.review_id=?;";
        List<LikeReview> list = jdbcTemplate.query(query, this::mapRowToLikeReview, reviewId);
        if(list.size()>0){
            return list.get(0);
        }else{

            return LikeReview.builder().reviewId(reviewId).useful(0).build();
        }
    }

    private LikeReview mapRowToLikeReview(ResultSet resultSet, int rowNum) throws SQLException {
        return LikeReview.builder()
                .reviewId(resultSet.getLong("review_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .build();
    }
}

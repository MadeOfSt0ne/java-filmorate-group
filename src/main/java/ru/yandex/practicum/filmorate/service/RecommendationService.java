package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Recommendation;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс-сервис для управления рекомендациями.
 */
@Service
public class RecommendationService {
    private final LikeStorage likeStorage;
    private final FilmService filmService;
    private final RecommendationStorage recommendationStorage;

    @Autowired
    RecommendationService(
            RecommendationStorage recommendationStorage,
            FilmService filmService,
            LikeStorage databaseFilmStorage) {
        this.recommendationStorage = recommendationStorage;
        this.likeStorage = databaseFilmStorage;
        this.filmService = filmService;
    }

    /**
     * Рассчитывает рекомендации для всех пользователей. Наивная реализация O(n^2).
     * Метод тяжеловесный предполагает запуск в фоне по расписанию или событию.
     *
     * @param maxSizeOfRecommendations макс. кол-во фильмов для пользователя
     */
    public void compute(int maxSizeOfRecommendations) {
        Map<Long, Set<Long>> usersLikes = likeStorage.getUsersLikesMap();
        Map<Long, Set<Long>> similarUsers = new HashMap<>();

        for (Map.Entry<Long, Set<Long>> i : usersLikes.entrySet()) {
            for (Map.Entry<Long, Set<Long>> j : usersLikes.entrySet()) {
                if (Objects.equals(i.getKey(), j.getKey())) continue;

                Set<Long> intersect = new HashSet<>(i.getValue());
                intersect.retainAll(j.getValue());

                if (intersect.size() > 0 && j.getValue().size() > i.getValue().size()) {
                    similarUsers.merge(i.getKey(), new HashSet<>(Set.of(j.getKey())), (oldVal, newVal) -> {
                        oldVal.add(j.getKey());
                        return oldVal;
                    });
                }
            }
        }

        Map<Long, Set<Long>> usersRecommendations = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> i : similarUsers.entrySet()) {
            Set<Long> currentUserFilms = usersLikes.get(i.getKey());
            Set<Long> recommendations = new HashSet<>();
            for (Long userId : i.getValue()) {
                if (recommendations.size() > maxSizeOfRecommendations) break;
                Set<Long> userFilms = usersLikes.get(userId);
                userFilms.removeAll(currentUserFilms);
                recommendations.addAll(userFilms);
            }
            usersRecommendations.put(i.getKey(), recommendations);
        }

        for (Map.Entry<Long, Set<Long>> i : usersRecommendations.entrySet()) {
            recommendationStorage.save(Recommendation.builder().whomId(i.getKey()).filmsIds(i.getValue()).build());
        }
    }

    /**
     * Получает рекомендации фильмов для пользователя.
     *
     * @param id уникальный идентификатор польз.
     * @return список рекомендованных фильмов
     */
    public List<Film> getFilmRecommendationsByUserId(Long id) {
        return recommendationStorage.getRecommendationByUserId(id).getFilmsIds().stream().map(filmService::getFilm)
                .collect(Collectors.toList());
    }
}

package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
class RecommendationServiceTest {
    private final FilmService filmService;
    private final UserService userService;
    private final RecommendationService recommendationService;

    @Autowired
    RecommendationServiceTest(FilmService filmService, UserService userService, RecommendationService recommendationService) {
        this.filmService = filmService;
        this.userService = userService;
        this.recommendationService = recommendationService;
    }

    private static final List<Film> films = List.of(
            genNewFilm(0L, "Криминальное чтиво"),
            genNewFilm(1L, "Джанго освобожденный"),
            genNewFilm(2L, "Омерзительная восьмерка"),
            genNewFilm(3L, "Убить Билла: Кровавое дело целиком"),
            genNewFilm(4L, "Бешеные псы")
    );

    private static final List<User> users = List.of(
            genNewUser(0L, "James"), genNewUser(1L, "Mary"), genNewUser(2L, "Robert")
    );

    @Test
    void contextLoads() {
        assertNotNull(filmService);
    }

    @Test
    void testGetRecommendations() {
        films.forEach(filmService::addFilm);
        users.forEach(userService::addUser);

        filmService.addLikeToFilm(0L, 0L);
        filmService.addLikeToFilm(1L, 0L);
        filmService.addLikeToFilm(2L, 0L);

        filmService.addLikeToFilm(3L, 1L);
        filmService.addLikeToFilm(4L, 1L);

        filmService.addLikeToFilm(0L, 2L);

        recommendationService.compute(5);

        assertEquals(List.of(films.get(1), films.get(2)), recommendationService.getFilmRecommendationsByUserId(2L));
    }

    private static Film genNewFilm(Long id, String title) {
        return Film.builder().id(id).name(title).description("").mpa(MpaRating.builder().id(1).title("G").build())
                .duration(0).releaseDate(LocalDate.of(1970, 1, 1)).build();
    }

    private static User genNewUser(Long id, String name) {
        return User.builder().id(id).login(name).name(name).email(name + "@yandex.ru")
                .birthday(LocalDate.of(1970, 1, 1)).build();
    }
}

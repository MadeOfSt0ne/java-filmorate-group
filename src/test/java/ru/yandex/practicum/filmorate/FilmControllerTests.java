package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmController filmController;

    @AfterEach
    void cleanup() {
        filmController.clear();
    }

    @Test
    void contextLoads() {
        assertThat(filmController).isNotNull();
    }

    @Test
    void testGetWithoutFilms() throws Exception {
        mockMvc.perform(get("/films")).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testGetWithFilms() throws Exception {
        filmController.create(new Film("test", "test", 1, new Date(0)));

        final String exceptedJson =
                "[{\"name\":\"test\",\"description\":\"test\",\"duration\":1,\"releaseDate\":\"1970-01-01\"}]";

        mockMvc.perform(get("/films")).andExpect(status().isOk())
                .andExpect(content().json(exceptedJson));
    }

    @Test
    void testCreateCorrectFilm() throws Exception {
        final String json =
                "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\"," + "\"releaseDate\":\"1967" + "-03-25\","
                        + "\"duration\":100}";

        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateFilmWithIncorrectDate() throws Exception {
        final String json =
                "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\"," + "\"releaseDate\":\"1767" + "-03-25\","
                        + "\"duration\":100}";

        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateFilmWithIncorrectDuration() throws Exception {
        final String json =
                "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\"," + "\"releaseDate\":\"1967" + "-03-25\","
                        + "\"duration\":-100}";

        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateFilmWithTooLongDescription() throws Exception {
        final String json = "{\"name\":\"nisi eiusmod\",\"releaseDate\":\"1967-03-25\",\"duration\":100," +
                "\"description\":\"adipisicingadipisicingadipisicingadipisicingadipisicingadipisicinadipisicingg" +
                "adipisicingadipisicingadipisicingadipisicingadipisicingadipisicingadipisicingadipisadipisdicing" +
                "adipisicingadipisicingadipisicingadipisicingadipisicingadipisicing\"}";

        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateFilmWithoutName() throws Exception {
        final String json = "{\"description\":\"adipisicing\"," + "\"releaseDate\":\"1967" + "-03-25\"," +
                "\"duration\":100}";

        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateFilm() throws Exception {
        var film = filmController.create(new Film("test", "test", 1, new Date(0)));

        final String json = "{\"id\":" + film.getId() + ",\"name\":\"test\",\"description\":\"test\"," +
                "\"duration\":120,\"releaseDate\":\"1970-01-01\"}";

        mockMvc.perform(put("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films")).andExpect(status().isOk())
                .andExpect(content().json("[" + json + "]"));
    }

    @Test
    void testUpdateNonExistedFilm() throws Exception {
        final String json = "{\"id\":15,\"name\":\"test\",\"description\":\"test\",\"duration\":120,"
            + "\"releaseDate\":\"1970-01-01\"}";

        mockMvc.perform(put("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

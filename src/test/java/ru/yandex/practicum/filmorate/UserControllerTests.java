package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @AfterEach
    void cleanup() {
        userController.clear();
    }

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    @Test
    void testGetWithoutUsers() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testGetWithUsers() throws Exception {
        userController.create(new User("test@test.ru", "test", "test", new Date(0L)));

        final String exceptedJson = "[{\"email\":\"test@test.ru\",\"login\":\"test\",\"name\":\"test\"," +
                "\"birthday\":\"1970-01-01\"}]";

        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(content().json(exceptedJson));
    }

    @Test
    void testCreateCorrectUser() throws Exception {
        final String json = "{\"login\":\"dolore_ullamco\",\"name\":\"est adipisicing\",\"email\":\"mail@mail.ru\"," +
                "\"birthday\":\"1946-08-20\"}";

        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUserWithBdayinFuture() throws Exception {
        final String json = "{\"login\":\"dolore_ullamco\",\"name\":\"est adipisicing\",\"email\":\"mail@mail.ru\"," +
                "\"birthday\":\"2055-08-20\"}";

        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithIncorrectLogin() throws Exception {
        final String json = "{\"login\":\"dolore ullamco\",\"name\":\"est adipisicing\",\"email\":\"mail@mail.ru\"," +
                "\"birthday\":\"1946-08-20\"}";

        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithIncorrectEmail() throws Exception {
        final String json = "{\"login\":\"dolore_ullamco\",\"name\":\"est adipisicing\",\"email\":\"mailmail.ru\"," +
                "\"birthday\":\"1946-08-20\"}";

        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithoutLogin() throws Exception {
        final String json = "{\"name\":\"est adipisicing\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}";

        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        var user = userController.create(new User("test@test.ru", "test", "test", new Date(0L)));

        final String json = "{\"id\":" + user.getId() + ",\"email\":\"new@test.ru\",\"login\":\"test\","
                + "\"name\":\"test\",\"birthday\":\"1970-01-01\"}";

        mockMvc.perform(put("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(content().json("[" + json + "]"));
    }

    @Test
    void testUpdateNonExistedUser() throws Exception {
        final String json = "{\"id\":555,\"email\":\"new@test.ru\",\"login\":\"test\","
                + "\"name\":\"test\",\"birthday\":\"1970-01-01\"}";

        mockMvc.perform(put("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController userController;

    @Autowired
    private UserStorage userStorage;

    private final User user1 = User.builder().login("test1").email("test1@test.ru").birthday(new Date(0L)).build();
    private final User user2 = User.builder().login("test2").email("test2@test.ru").birthday(new Date(0L)).build();

    @AfterEach
    void tearDown() {
        userStorage.clear();
    }

    @Test
    void contextLoads() {
        assertNotNull(userController);
    }

    @Test
    void testGetAllWithoutUsers() {
        assertEquals(Collections.EMPTY_LIST, new ArrayList<>(userController.getAll()));
    }

    @Test
    void testGetAllWithUsers() {
        final User user2 = userController.create(user1);
        assertEquals(List.of(user2), new ArrayList<>(userController.getAll()));
    }

    @Test
    void testGetNonExistUser() {
        final User user2 = userController.create(user1);
        userStorage.remove(user2);
        assertThrows(NoSuchElementException.class, () -> userController.get(user2.getId()));
    }

    @Test
    void testCreateCorrectUser() {
        final User user2 = userController.create(user1);
        assertEquals(user2, userController.get(user2.getId()));
    }

    @Test
    void testCreateUserWithNegativeId() {
        assertThrows(ValidationException.class, () -> userController.create(user1.toBuilder().id(-1L).build()));
    }

    @Test
    void testCreateUserWithBirthdayInFuture() {
        assertThrows(ConstraintViolationException.class, () -> userController.create(user1.toBuilder()
                .birthday(Date.from(Instant.parse("2055-01-01T00:00:00.00Z"))).build()));
    }

    @Test
    void testCreateUserWithIncorrectLogin() {
        assertThrows(ConstraintViolationException.class,
                () -> userController.create(user1.toBuilder().login("test test").build()));
    }

    @Test
    void testCreateUserWithIncorrectEmail() {
        assertThrows(ConstraintViolationException.class,
                () -> userController.create(user1.toBuilder().email("test.ru").build()));
    }

    @Test
    void testCreateUserWithoutLogin() {
        assertThrows(ConstraintViolationException.class,
                () -> userController.create(user1.toBuilder().login("").build()));
    }

    @Test
    void testUpdateUser() {
        final User user2 = userController.create(user1);
        final User user3 = user2.toBuilder().login("test3").build();
        userController.update(user3);
        assertEquals(user3, userController.get(user3.getId()));
    }

    @Test
    void testUpdateNonExistedUser() {
        assertThrows(NoSuchElementException.class, () -> userController.update(user1));
    }

    @Test
    void testAddToFriends() {
        final User user3 = userController.create(user1);
        final User user4 = userController.create(user2);
        userController.addFriends(user3.getId(), user4.getId());
        assertEquals(List.of(user4), userController.getFriends(user3.getId()));
        assertEquals(List.of(user3), userController.getFriends(user4.getId()));
    }

    @Test
    void testAddToNonExistFriend() {
        final User user2 = userController.create(user1);
        assertThrows(NoSuchElementException.class, () -> userController.addFriends(user2.getId(), 2L));
    }

    @Test
    void testRemoveFromFriends() {
        final User user3 = userController.create(user1);
        final User user4 = userController.create(user2);
        userController.addFriends(user3.getId(), user4.getId());
        userController.deleteFriends(user3.getId(), user4.getId());
        assertEquals(Collections.EMPTY_LIST, userController.getFriends(user3.getId()));
    }

    @Test
    void testGetCommonFriends() {
        final User user3 = userController.create(user1);
        final User user4 = userController.create(user2);
        final User user5 = userController.create(user2);
        userController.addFriends(user3.getId(), user4.getId());
        userController.addFriends(user3.getId(), user5.getId());
        userController.addFriends(user4.getId(), user5.getId());
        assertEquals(List.of(user3), userController.getCommonFriends(user4.getId(), user5.getId()));
    }
}

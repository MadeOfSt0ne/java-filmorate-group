package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * REST-контроллер для пользователей.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserStorage storage;
    private final UserService service;

    @GetMapping
    Collection<User> getAll() {
        return storage.getAll();
    }

    @GetMapping("{id}")
    User get(@PathVariable Long id) {
        User user = storage.get(id);
        if (user == null) throw new NoSuchElementException();
        return user;
    }

    @PostMapping
    User create(@Valid @RequestBody User user) {
        user = UserValidator.validate(user);
        log.info("CREATE {}", user);
        storage.add(user);
        return user;
    }

    @PutMapping
    User update(@Valid @RequestBody User user) {
        user = UserValidator.validate(user);
        log.info("UPDATE {}", user);
        storage.update(user);
        return user;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    Collection<User> getFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getFriendsIntersection(id, otherId);
    }

    @GetMapping("{id}/friends")
    Collection<User> getFriends(@PathVariable Long id) {
        return storage.get(id).getFriends().stream().map(storage::get).collect(Collectors.toList());
    }

    @PutMapping("{id}/friends/{friendId}")
    void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriends(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriends(id, friendId);
    }
}

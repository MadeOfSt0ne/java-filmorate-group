package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventsService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.Collection;

/**
 * REST-контроллер для пользователей.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final EventsService eventsService;

    @GetMapping
    Collection<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    User get(@PathVariable final Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    User create(@Valid @RequestBody final User user) {
        final User validatedUser = UserValidator.validate(user);
        log.info("CREATE {}", validatedUser);
        userService.addUser(validatedUser);
        return validatedUser;
    }

    @PutMapping
    User update(@Valid @RequestBody final User user) {
        final User validatedUser = UserValidator.validate(user);
        log.info("UPDATE {}", validatedUser);
        userService.updateUser(validatedUser);
        return validatedUser;
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable final Long id) {
        userService.removeUser(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    Collection<User> getCommonFriends(@PathVariable final Long id, @PathVariable final Long otherId) {
        return userService.getFriendsIntersection(id, otherId);
    }

    @GetMapping("{id}/friends")
    Collection<User> getFriends(@PathVariable final Long id) {
        return userService.getUserFriends(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    void addFriends(@PathVariable final Long id, @PathVariable final Long friendId) {
        log.info("USER ({}) ADDS USER ({}) TO FRIENDS", id, friendId);
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    void deleteFriends(@PathVariable final Long id, @PathVariable final Long friendId) {
        log.info("USER ({}) REMOVES USER ({}) FROM FRIENDS", id, friendId);
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getEvents(@PathVariable("id") Long id){
        return eventsService.getEvents(id);
    }
}

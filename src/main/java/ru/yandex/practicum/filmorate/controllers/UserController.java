package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

/**
 * REST-контроллер для User'ов.
 *
 * Для валидации объектов целиком полагается на спецификацию JSR-303.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("CREATE {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("UPDATE {}", user);

        if (!users.containsKey(user.getId())) {
            log.warn("Can't find obj with id={}", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return users.put(user.getId(), user);
    }

    /**
     * Сбрасывает внут. состояние, очищает хранилище.
     * Используется только при интегр. тестировании.
     */
    public void clear() {
        this.users.clear();
    }
}

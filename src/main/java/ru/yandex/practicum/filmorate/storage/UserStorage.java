package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User get(Long id);

    void add(User user);

    void update(User user);

    void remove(User user);
}

package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.IdProvider;

public class UserValidator {
    public static User validate(User user) {
        if (user.getId() == null) {
            user = user.toBuilder().id(IdProvider.getNextLongId(User.class)).build();
        }

        if (user.getName() == null) {
            user = user.toBuilder().name(user.getLogin()).build();
        }

        if (user.getId() < 0) throw new ValidationException("user id must be greater than zero");

        return user;
    }
}

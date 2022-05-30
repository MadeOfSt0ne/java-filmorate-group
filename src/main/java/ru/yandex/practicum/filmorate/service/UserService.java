package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс-сервис для управления пользователями.
 */
@Service
public class UserService {
    private final UserStorage userStorage;

    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserStorage databaseUserStorage, FriendshipStorage databaseFriendshipStorage) {
        this.userStorage = databaseUserStorage;
        this.friendshipStorage = databaseFriendshipStorage;
    }

    /**
     * Получает всех пользователей.
     *
     * @return коллекция пользователей
     */
    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return объект пользователя
     * @throws NoSuchElementException - если пользователя не существует.
     */
    public User getUser(final Long id) {
        final User user = userStorage.get(id);
        if (user == null) throw new NoSuchElementException();
        return user;
    }

    /**
     * Добавляет пользователя.
     *
     * @param newUser новый пользователь
     */
    public void addUser(final User newUser) {
        userStorage.add(newUser);
    }

    /**
     * Обновляет пользователя.
     *
     * @param updatedUser обновляемый пользователь
     * @throws NoSuchElementException - если пользователя не существует.
     */
    public void updateUser(final User updatedUser) {
        final User user = getUser(updatedUser.getId());
        if (updatedUser.equals(user)) return;
        userStorage.update(updatedUser);
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @throws NoSuchElementException - если пользователя не существует.
     */
    public void removeUser(final Long id) {
        userStorage.remove(getUser(id));
    }

    /**
     * Получает список друзей пользователя.
     *
     * @param id уникальный идентификатор пользователя
     * @return коллекция друзей пользователя
     * @throws NoSuchElementException - если пользователя не существует.
     */
    public Collection<User> getUserFriends(final Long id) {
        return friendshipStorage.getUserFriendsIds(getUser(id).getId()).stream().map(userStorage::get).collect(Collectors.toList());
    }

    /**
     * Взаимно добавляет друг друга в друзья у каждого из пользователей.
     *
     * @param id       уникальный идентификатор пользователя
     * @param friendId уникальный идентификатор друга
     * @throws NoSuchElementException - если пользователя не существует.
     */
    public void addFriends(final Long id, final Long friendId) {
        User user1 = getUser(id);
        User user2 = getUser(friendId);

        friendshipStorage.save(Friendship.builder().user(user1).friend(user2).build());
    }

    /**
     * Взаимно удаляет друг друга из друзей у каждого из пользователей.
     *
     * @param id       уникальный идентификатор пользователя
     * @param friendId уникальный идентификатор друга
     * @throws NoSuchElementException - если пользователя не существует.
     */
    public void deleteFriends(Long id, Long friendId) {
        User user1 = getUser(id);
        User user2 = getUser(friendId);

        friendshipStorage.delete(Friendship.builder().user(user1).friend(user2).build());
    }

    /**
     * Получает список общих друзей между двумя пользователями.
     *
     * @param id      уникальный идентификатор пользователя
     * @param otherId уникальный идентификатор польз.
     * @return коллекция общих друзей пользователей
     * @throws NoSuchElementException - если пользователя не существует.
     */
    public Collection<User> getFriendsIntersection(Long id, Long otherId) {
        User user1 = getUser(id);
        User user2 = getUser(otherId);

        Set<Long> intersection = new HashSet<>(friendshipStorage.getUserFriendsIds(user1.getId()));
        intersection.retainAll(friendshipStorage.getUserFriendsIds(user2.getId()));

        return intersection.stream().map(userStorage::get).collect(Collectors.toList());
    }
}

Групповой проект. Команда №1.

Реализованная функциональность:
1. «Поиск»
2. «Рекомендации»
3. «Лента событий»
4. «Общие фильмы»
5. «Отзывы»
6. Удаление фильмов и пользователей

![schema](schema.png)

```sql
-- 1. добавление пользователя
INSERT INTO `users` (`email`, `login`, `name`, `birthday`) VALUES ('root@localhost', 'root', '', '1970-01-01')

-- 2. обновление пользователя
UPDATE `users` SET `name` = 'root' WHERE `user_id` = ?

-- 3. удаление пользователя (+ нам надо удалить пользователя из всех друзей)
DELETE `users` WHERE `user_id` = ?

-- 4. добавление пользователя в друзья
INSERT INTO `friends` (`user_id`, `friend_id`) VALUES (?, ?)

-- 5. удаление пользователя из друзей
DELETE FROM `friends` WHERE `user_id` = ? AND `friend_id` = ?

-- 6. поиск друзей пользователя
SELECT * FROM `users` WHERE `user_id` IN
    (SELECT `friend_id` FROM `friends` WHERE `user_id` = ?)
  OR `user_id` IN
    (SELECT `user_id` FROM `friends` WHERE `friend_id` = ?)

-- 7. поиск общих друзей
SELECT * FROM `users` WHERE `user_id` IN
    (SELECT `friend_id` FROM `friends` WHERE `user_id` = ?)
  OR `user_id` IN
    (SELECT `user_id` FROM `friends` WHERE `friend_id` = ?)
INNER JOIN
(SELECT * FROM `users` WHERE `user_id` IN
    (SELECT `friend_id` FROM `friends` WHERE `user_id` = ?)
  OR `user_id` IN
    (SELECT `user_id` FROM `friends` WHERE `friend_id` = ?)) `users2` ON `users`.`user_id` = `users2`.`user_id`

-- 8. добавление фильма
INSERT INTO `films` (`name`, `description`, `release_date`, `duration`, `mpaa_id`) VALUES ('Test', '...', '2022-05-15', 3600, 1)

-- 9. обновление фильма
UPDATE `films` SET `description` = 'test' WHERE `film_id` = ?

-- 10. удаление фильма
DELETE FROM `films` WHERE `film_id` = ?

-- 11. добавление лайка фильму
INSERT INTO `likes` (`user_id`, `film_id`) VALUES (?, ?)

-- 12. удаление лайка с фильма
DELETE FROM `likes` WHERE `user_id` = ? AND `film_id` = ?

-- 13. список популярных фильмов
SELECT `films`.`film_id`, COUNT(`likes`.`user_id`) `likes_count`
FROM `films`
LEFT JOIN `likes` ON `films`.`film_id` = `likes`.`film_id`
GROUP BY `films`.`film_id`
ORDER BY `likes_count` DESC

-- 14. поиск жанров фильма
SELECT *
FROM `films`
INNER JOIN `film_genres` ON `film_genres`.`film_id` = `films`.`film_id`
INNER JOIN `genres` ON `genres`.`genre_id` = `film_genres`.`genre_id`
```

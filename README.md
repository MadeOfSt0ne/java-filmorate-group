![schema](schema.png)

```sql
-- добавление в друзья
INSERT INTO friendship (who, whom)
VALUES (?, ?)

-- друзья пользователя
SELECT *
FROM users
WHERE id IN
    (SELECT whom
     FROM friendships
     WHERE who = ?)
   OR id IN
    (SELECT who
     FROM friendships
     WHERE whom = ?)

-- общие друзья пользователей
SELECT *
FROM users
WHERE id IN
    (SELECT whom
     FROM friendships
     WHERE who = ?)
   OR id IN
    (SELECT who
     FROM friendships
     WHERE whom = ?)
INTERSECT
SELECT *
FROM users
WHERE id IN
    (SELECT whom
     FROM friendships
     WHERE who = ?)
   OR id IN
    (SELECT who
     FROM friendships
     WHERE whom = ?)

-- популярные фильмы
SELECT films.id,
  COUNT(likes.user_id) likes_count
FROM films
LEFT JOIN likes ON films.id = likes.film_id
GROUP BY films.id
ORDER BY likes_count DESC
```

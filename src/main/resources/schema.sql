CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id BIGINT auto_increment NOT NULL PRIMARY KEY,
    title  VARCHAR(255)          NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT auto_increment NOT NULL PRIMARY KEY,
    name         VARCHAR(255)          NOT NULL,
    description  VARCHAR(200)          NOT NULL,
    release_date DATE                  NOT NULL,
    duration     BIGINT                NOT NULL,
    mpa_id       INT                   NULL,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id BIGINT auto_increment NOT NULL PRIMARY KEY,
    title    VARCHAR(255)          NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT auto_increment NOT NULL PRIMARY KEY,
    email    VARCHAR(255)          NOT NULL UNIQUE,
    login    VARCHAR(255)          NOT NULL UNIQUE,
    name     VARCHAR(255)          NOT NULL,
    birthday DATE                  NOT NULL
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS recommendations
(
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id)
);


CREATE TABLE IF NOT EXISTS events
(
    event_id BIGINT auto_increment PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(255)  NOT NULL,
    operation VARCHAR(255)  NOT NULL,
    time_stamp BIGINT NOT NULL,
    entity_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review
(
    review_id  LONG auto_increment NOT NULL PRIMARY KEY,
    content    VARCHAR(255)          NOT NULL,
    is_positive BOOLEAN              NOT NULL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS review_like
(
    is_useful BOOLEAN              NOT NULL,
    user_id BIGINT NOT NULL,
    review_id LONG NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES review (review_id) ON DELETE CASCADE
);

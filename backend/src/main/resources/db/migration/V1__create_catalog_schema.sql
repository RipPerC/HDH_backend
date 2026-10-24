CREATE SCHEMA IF NOT EXISTS data;

CREATE SEQUENCE IF NOT EXISTS data.film_id_seq AS bigint START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS data.serie_id_seq AS bigint START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS data.episode_id_seq AS bigint START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS data.film (
    id bigint NOT NULL PRIMARY KEY,
    title varchar(255),
    director varchar(255),
    year integer,
    audio varchar(255),
    subtitle varchar(255),
    format varchar(255),
    downloaded boolean,
    creation_date timestamp
);

CREATE TABLE IF NOT EXISTS data.serie (
    id bigint NOT NULL PRIMARY KEY,
    title varchar(255),
    year integer,
    downloaded boolean,
    web varchar(255)
);

CREATE TABLE IF NOT EXISTS data.episode (
    id bigint NOT NULL PRIMARY KEY,
    fk_serie bigint NOT NULL REFERENCES data.serie (id),
    season integer,
    episode integer,
    audio varchar(255),
    subtitle varchar(255)
);

SELECT setval('data.film_id_seq', COALESCE((SELECT MAX(id) FROM data.film), 0) + 1, false);
SELECT setval('data.serie_id_seq', COALESCE((SELECT MAX(id) FROM data.serie), 0) + 1, false);
SELECT setval('data.episode_id_seq', COALESCE((SELECT MAX(id) FROM data.episode), 0) + 1, false);

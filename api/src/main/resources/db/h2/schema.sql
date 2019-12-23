-- CREATE SCHEMA IF NOT EXISTS social_network;
--
-- SET SCHEMA social_network;

DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS friend;

CREATE TABLE IF NOT EXISTS role (
  id          BIGINT IDENTITY PRIMARY KEY,
  name        VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS person (
  id         BIGINT IDENTITY PRIMARY KEY,
  first_name VARCHAR(50)        NOT NULL,
  last_name  VARCHAR(50)        NOT NULL,
  short_name VARCHAR(50),
  email      VARCHAR(50) UNIQUE NOT NULL,
  password   VARCHAR(60)        NOT NULL,
  phone      VARCHAR(15),
  birth_date DATE,
  gender     INT                NOT NULL,
  created    DATETIME           NOT NULL
);

CREATE TABLE IF NOT EXISTS user_role (
  person_id BIGINT NOT NULL,
  role_id   BIGINT NOT NULL,
  FOREIGN KEY (person_id) REFERENCES person (id),
  FOREIGN KEY (role_id) REFERENCES role (id),
);

CREATE TABLE IF NOT EXISTS friend (
  person_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  FOREIGN KEY (person_id) REFERENCES person (id),
  FOREIGN KEY (friend_id) REFERENCES person (id),
);

CREATE TABLE IF NOT EXISTS message (
  id           BIGINT IDENTITY PRIMARY KEY,
  posted       DATETIME NOT NULL,
  sender_id    BIGINT   NOT NULL,
  recipient_id BIGINT   NOT NULL,
  body         VARCHAR(1000),
  FOREIGN KEY (sender_id) REFERENCES person (id),
  FOREIGN KEY (recipient_id) REFERENCES person (id),
);
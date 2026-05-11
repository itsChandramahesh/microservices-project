CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 3 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_id_seq'),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL DEFAULT 'changeme'
);

ALTER TABLE users
    ALTER COLUMN id SET DEFAULT nextval('users_id_seq');

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password VARCHAR(255) NOT NULL DEFAULT 'changeme';

SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1), true);

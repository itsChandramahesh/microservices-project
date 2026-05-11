INSERT INTO users (id, name, email, password)
VALUES
    (1, 'User-1', 'user1@example.com', '$2a$10$h0bW3P6f8d/2Q0vh3A6b3uEtZ6zQnUnxE27XGr0CcsMEY0S8mVKGK'),
    (2, 'User-2', 'user2@example.com', '$2a$10$h0bW3P6f8d/2Q0vh3A6b3uEtZ6zQnUnxE27XGr0CcsMEY0S8mVKGK')
ON CONFLICT (id) DO NOTHING;

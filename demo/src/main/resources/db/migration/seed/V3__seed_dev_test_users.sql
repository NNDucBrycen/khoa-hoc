-- V3: Seed dev/test users (dev and test profiles only — loaded via spring.flyway.locations per profile)
-- BCrypt hashes were computed offline (cost factor 12, bcryptjs):
--   admin   / Admin@123 -> $2b$12$1JRIcBa7NqCfQcwH75Aea.L06n6gWRpR54tGK/W4UvGjScseWiAma
--   user01  / User@123  -> $2b$12$TJqx3Tzdl0ZzRNVW6s7HP.GJOBUhLqtTWlHqZ/yoMWvkT3tqH/oUy

INSERT INTO roles (code, name) VALUES
    ('ADMIN', 'Administrator'),
    ('USER',  'Regular User')
ON CONFLICT (code) DO NOTHING;

INSERT INTO users (username, password_hash, enabled) VALUES
    ('admin',  '$2b$12$1JRIcBa7NqCfQcwH75Aea.L06n6gWRpR54tGK/W4UvGjScseWiAma', TRUE),
    ('user01', '$2b$12$TJqx3Tzdl0ZzRNVW6s7HP.GJOBUhLqtTWlHqZ/yoMWvkT3tqH/oUy', TRUE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.code = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user01' AND r.code = 'USER'
ON CONFLICT DO NOTHING;

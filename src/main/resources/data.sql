INSERT IGNORE INTO roles (id_role, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id_role, name) VALUES (2, 'ROLE_USER');

INSERT IGNORE INTO users (id, username, email, password)
VALUES (1, 'paula', 'paulafa8@hotmail.com', 
        '$2a$12$gO7qsqwroL6uHdRb7kKk3udxMtVBd9XIN0dMuTF.V.wMINyY3WYau');

INSERT IGNORE INTO roles_users (user_id, role_id)
VALUES (1, 1); -- 1 = ROLE_ADMIN


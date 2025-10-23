INSERT IGNORE INTO roles (id_role, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id_role, name) VALUES (2, 'ROLE_USER');

INSERT IGNORE INTO users (id, username, email, password)
VALUES (1, 'paula', 'paulafa8@hotmail.com', 
        '$2a$12$gO7qsqwroL6uHdRb7kKk3udxMtVBd9XIN0dMuTF.V.wMINyY3WYau');

INSERT IGNORE INTO roles_users (user_id, role_id)
VALUES (1, 1); -- 1 = ROLE_ADMIN

-- Tabla de pedidos (check if exists)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    item_count INT DEFAULT 0,
    notes VARCHAR(500)
);

-- Tabla de items (check if exists)
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
);

-- Make sure stock table exists too
CREATE TABLE IF NOT EXISTS stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_stock DECIMAL(10,2) DEFAULT 0,
    minimum_stock DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(50)
);

-- Insert sample stock data
INSERT IGNORE INTO stock (name, current_stock, minimum_stock, unit) VALUES
('Tomatoes', 2.5, 5.0, 'Kg'),
('Mozzarella', 4.0, 3.0, 'Kg'),
('Olive Oil', 1.2, 2.0, 'L'),
('Flour', 8.0, 5.0, 'Kg'),
('Onions', 3.0, 4.0, 'Kg');
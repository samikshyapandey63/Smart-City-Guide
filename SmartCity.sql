CREATE DATABASE IF NOT EXISTS smartcity;
USE smartcity;

CREATE TABLE users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(10)  NOT NULL DEFAULT 'TOURIST'
);

CREATE TABLE categories (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE places (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    address     VARCHAR(200) NOT NULL,
    area        VARCHAR(100),
    phone       VARCHAR(15),
    open_time   TIME NOT NULL,
    close_time  TIME NOT NULL,
    description TEXT,
    UNIQUE (name, address),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE reviews (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    place_id   INT NOT NULL,
    user_id    INT NOT NULL,
    rating     INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (place_id, user_id),
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE
);

CREATE TABLE favorites (
    user_id  INT NOT NULL,
    place_id INT NOT NULL,
    PRIMARY KEY (user_id, place_id),
    FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE
);

CREATE TABLE suggestions (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    address     VARCHAR(200) NOT NULL,
    area        VARCHAR(100),
    phone       VARCHAR(15),
    open_time   TIME NOT NULL,
    close_time  TIME NOT NULL,
    description TEXT,
    status      VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Sample data ---------------------------------------------------------
INSERT INTO users (name, username, password, role) VALUES
    ('Administrator', 'admin', 'admin123', 'ADMIN'),
    ('Ram Sharma',    'ram',   'ram123',   'TOURIST');

INSERT INTO categories (name) VALUES
    ('Hospital'), ('Hotel'), ('Restaurant'), ('ATM'),
    ('Pharmacy'), ('Tourist Spot'), ('Transport');

INSERT INTO places (name, category_id, address, area, phone, open_time, close_time, description) VALUES
    ('City Hospital',    1, 'Main Road',     'Central',  '0123456789', '00:00', '23:59', 'Emergency and general care.'),
    ('Central Pharmacy', 5, 'Market Street', 'Central',  '0987654321', '08:00', '21:00', 'Medicines and health supplies.'),
    ('Hotel Sunrise',    2, 'Lake Side',     'Lakeside', '0111222333', '00:00', '23:59', 'Rooms with a lake view.');

INSERT INTO suggestions (user_id, name, category_id, address, area, phone, open_time, close_time, description) VALUES
    (2, 'Green Leaf Cafe', 3, 'Park Road', 'Central', '0444555666', '07:00', '20:00', 'Cosy cafe near the park.');
 
INSERT INTO users (name, email)
VALUES ('Иван Иванов', 'ivan.ivanov@example.com'),
       ('Мария Петрова', 'maria.petrova@example.com'),
       ('Алексей Сидоров', 'alexey.sidorov@example.com'),
       ('Елена Смирнова', 'elena.smirnova@example.com'),
       ('Дмитрий Кузнецов', 'dmitry.kuznetsov@example.com'),
       ('Ольга Васильева', 'olga.vasilyeva@example.com'),
       ('Сергей Павлов', 'sergey.pavlov@example.com'),
       ('Анна Козлова', 'anna.kozlova@example.com'),
       ('Михаил Орлов', 'mikhail.orlov@example.com'),
       ('Наталья Лебедева', 'natalya.lebedeva@example.com');

INSERT INTO orders (user_id, status, amount)
VALUES ( 1, 'CONFIRMED', 54999.99),
       ( 1, 'PAID', 79999.99),
       ( 1, 'CREATED', 12999.99),

       ( 2, 'PAID', 74999.99),
       ( 2, 'CONFIRMED', 41999.99),

       ( 3, 'PAID', 75999.99),

       ( 4, 'CONFIRMED', 34999.99),
       ( 4, 'CREATED', 8999.99),

       ( 5, 'PAID', 3999.99),
       ( 5, 'CONFIRMED', 49999.99),
       ( 5, 'CREATED', 5999.99),

       ( 6, 'PAID', 3499.99),

       ( 7, 'CANCELED', 1999.99),
       ( 7, 'CONFIRMED', 5499.99),

       ( 8, 'PAID', 2999.99),
       ( 8, 'CONFIRMED', 12999.99),
       ( 8, 'CREATED', 1999.99),

       ( 9, 'PAID', 4999.99),

       ( 10, 'CONFIRMED', 29999.99),
       ( 10, 'CREATED', 4999.99);

INSERT INTO order_products (order_id, product_name)
VALUES (1, 'Ноутбук Lenovo IdeaPad'),
       (1, 'Мышь беспроводная'),
       (2, 'Смартфон Samsung Galaxy'),
       (3, 'Наушники Sony'),
       (3, 'Чехол для телефона'),
       (3, 'Зарядное устройство'),

       (4, 'Планшет Apple iPad'),
       (4, 'Стилус'),
       (5, 'Умные часы Apple Watch'),

       (6, 'Игровая консоль PlayStation 5'),
       (6, 'Джойстик DualSense'),
       (6, 'Игра God of War'),
       (6, 'Игра Spider-Man 2'),

       (7, 'Кофемашина DeLonghi'),
       (7, 'Капсулы для кофе (20 шт)'),
       (8, 'Блендер Philips'),
       (8, 'Кухонные весы'),

       (9, 'Фитнес-браслет Xiaomi Mi Band'),
       (10, 'Беговая дорожка'),
       (11, 'Гантели 5 кг (2 шт)'),
       (11, 'Коврик для йоги'),

       (12, 'Книга "Чистый код"'),
       (12, 'Книга "Совершенный код"'),
       (12, 'Книга "Паттерны проектирования"'),

       (13, 'Зонт автоматический'),
       (14, 'Рюкзак городской'),
       (14, 'Косметичка'),

       (15, 'Набор отверток'),
       (16, 'Дрель электрическая'),
       (17, 'Набор сверл'),

       (18, 'Набор для рисования'),
       (18, 'Акварельные краски'),
       (18, 'Кисти (5 шт)'),
       (18, 'Бумага для акварели'),

       (19, 'Гироскутер'),
       (20, 'Защитный шлем'),
       (20, 'Наколенники');
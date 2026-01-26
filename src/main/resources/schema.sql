DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    username              VARCHAR(50) PRIMARY KEY,
    password              VARCHAR(255) NOT NULL,
    role                  VARCHAR(20)  NOT NULL,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    failed_attempt        INT DEFAULT 0
);

CREATE TABLE products
(
    product_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(50)    NOT NULL,
    description       VARCHAR(255),
    price             DECIMAL(10, 2) NOT NULL,
    quantity_in_stock INT            NOT NULL
);

CREATE TABLE customers
(
    customer_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(50) NOT NULL,
    last_name      VARCHAR(50) NOT NULL,
    email          VARCHAR(50) NOT NULL,
    contact_number BIGINT      NOT NULL,
    username       VARCHAR(50) UNIQUE,
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE orders
(
    order_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id      BIGINT         NOT NULL,
    order_date       DATE           NOT NULL,
    shipping_address VARCHAR(255)   NOT NULL,
    total_price      DECIMAL(10, 2) NOT NULL,
    order_status     VARCHAR(20)    NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
);

CREATE TABLE order_products
(
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS books
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    publication_year INT,
    author           VARCHAR(255) NOT NULL
);
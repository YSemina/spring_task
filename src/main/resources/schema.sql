DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;

CREATE TABLE authors (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         first_name VARCHAR(50),
                         last_name VARCHAR(50)
);

CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       publication_year INT,
                       page_count INT,
                       author_id BIGINT,
                       FOREIGN KEY (author_id) REFERENCES authors(id)
);

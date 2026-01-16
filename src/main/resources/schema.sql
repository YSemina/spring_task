CREATE TABLE departments
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE employees
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    position      VARCHAR(100),
    salary        DECIMAL(10, 2),
    department_id BIGINT
);
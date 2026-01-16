INSERT INTO departments (name)
VALUES ('IT Department'),
       ('HR Department'),
       ('Sales Department'),
       ('Finance Department'),
       ('Marketing Department');

INSERT INTO employees (first_name, last_name, position, salary, department_id)
VALUES
-- IT Department (id: 1)
('Иван', 'Иванов', 'Senior Java Developer', 150000.00, 1),
('Петр', 'Петров', 'Frontend Developer', 120000.00, 1),
('Сергей', 'Сидоров', 'DevOps Engineer', 140000.00, 1),

-- HR Department (id: 2)
('Мария', 'Кузнецова', 'HR Manager', 90000.00, 2),
('Анна', 'Смирнова', 'Recruiter', 80000.00, 2),

-- Sales Department (id: 3)
('Алексей', 'Попов', 'Sales Manager', 130000.00, 3),
('Ольга', 'Васильева', 'Sales Executive', 95000.00, 3),

-- Finance Department (id: 4)
('Дмитрий', 'Новиков', 'Financial Analyst', 125000.00, 4),
('Елена', 'Федорова', 'Accountant', 85000.00, 4),

-- Marketing Department (id: 5)
('Наталья', 'Морозова', 'Marketing Manager', 110000.00, 5);
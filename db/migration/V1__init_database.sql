CREATE TABLE employee
(
    id SERIAL PRIMARY KEY,
    lastName VARCHAR(25) NOT NULL,
    position VARCHAR(25) NOT NULL,
    birthDate DATE NOT NULL,
    hireDate DATE NOT NULL,
    departmentNumber INTEGER NOT NULL,
    salary DECIMAL(10, 2) NOT NULL
);

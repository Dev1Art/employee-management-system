CREATE TABLE employee
(
    id BIGSERIAL PRIMARY KEY,
    lastName VARCHAR NOT NULL,
    position VARCHAR NOT NULL,
    birthDate DATE NOT NULL,
    hireDate DATE NOT NULL,
    departmentNumber INTEGER NOT NULL,
    salary DECIMAL(7, 2) NOT NULL
);
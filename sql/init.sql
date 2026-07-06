CREATE DATABASE IF NOT EXISTS users_db;
CREATE DATABASE IF NOT EXISTS projects_db;
CREATE DATABASE IF NOT EXISTS tasks_db;

USE users_db;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BIT NOT NULL
);

USE projects_db;
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150),
    description VARCHAR(500),
    responsible_id BIGINT,
    status VARCHAR(50),
    progress INT,
    start_date DATE,
    end_date DATE
);

USE tasks_db;
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT,
    responsible_id BIGINT,
    title VARCHAR(150),
    description VARCHAR(500),
    status VARCHAR(50),
    progress INT,
    due_date DATE
);

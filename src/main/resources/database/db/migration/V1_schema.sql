CREATE TABLE IF NOT EXISTS titles (
    id SERIAL NOT NULL,
    name VARCHAR(50),
    user_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)

);

CREATE TABLE IF NOT EXISTS categories (
    id SERIAL NOT NULL,
    name VARCHAR(50),
    title_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (title_id) REFERENCES titles(id)
);

CREATE TABLE IF NOT EXISTS links (
    id SERIAL NOT NULL,
    link_name TEXT NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    category_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL NOT NULL,
    name VARCHAR(50) NOT NULL,
    username VARCHAR(15) NOT NULL,
    email VARCHAR(40) NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (username),
    UNIQUE (email)
);

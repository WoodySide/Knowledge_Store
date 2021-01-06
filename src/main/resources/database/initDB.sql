CREATE TABLE IF NOT EXISTS knowledge_data.titles (
    id SERIAL NOT NULL,
    name VARCHAR(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS knowledge_data.categories (
    id SERIAL NOT NULL,
    name VARCHAR(50),
    title_id int,
    PRIMARY KEY (id),
    FOREIGN KEY (title_id) REFERENCES knowledge_data.titles(id)
);

CREATE TABLE IF NOT EXISTS knowledge_data.links (
    id SERIAL NOT NULL,
    linkName TEXT NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    category_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES knowledge_data.categories(id)
);

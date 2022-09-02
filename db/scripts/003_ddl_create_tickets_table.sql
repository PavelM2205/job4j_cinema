CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES sessions(id),
    user_id INT NOT NULL REFERENCES users(id),
    pos_row INT NOT NULL,
    cell INT NOT NULL,
    CONSTRAINT ticket_unique UNIQUE (session_id, pos_row, cell)
);
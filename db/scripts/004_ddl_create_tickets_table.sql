CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES sessions(id),
    user_id INT NOT NULL REFERENCES users(id),
    place_id INT REFERENCES places(id),
    CONSTRAINT ticket_unique UNIQUE (session_id, place_id)
);
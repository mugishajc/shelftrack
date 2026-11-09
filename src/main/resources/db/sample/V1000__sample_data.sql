-- Sample catalogue for local demos. Only loaded with the "demo" profile.
INSERT INTO books (isbn, title, author, published_year, total_copies, available_copies, created_at) VALUES
    ('9780132350884', 'Clean Code', 'Robert C. Martin', 2008, 3, 3, CURRENT_TIMESTAMP),
    ('9780134685991', 'Effective Java', 'Joshua Bloch', 2018, 2, 2, CURRENT_TIMESTAMP),
    ('9780201633610', 'Design Patterns', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 1994, 1, 1, CURRENT_TIMESTAMP),
    ('9781617294945', 'Spring in Action', 'Craig Walls', 2018, 2, 2, CURRENT_TIMESTAMP),
    ('9780385474542', 'Things Fall Apart', 'Chinua Achebe', 1994, 4, 4, CURRENT_TIMESTAMP),
    ('9780143039952', 'The Odyssey', 'Homer', 1999, 2, 2, CURRENT_TIMESTAMP);

INSERT INTO members (full_name, email, phone, status, joined_on) VALUES
    ('Aline Uwase', 'aline.uwase@example.com', '+250788000001', 'ACTIVE', CURRENT_DATE),
    ('Eric Habimana', 'eric.habimana@example.com', '+250788000002', 'ACTIVE', CURRENT_DATE),
    ('Grace Mukamana', 'grace.mukamana@example.com', NULL, 'SUSPENDED', CURRENT_DATE);

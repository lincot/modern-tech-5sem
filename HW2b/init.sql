DROP TABLE TREES IF EXISTS;
CREATE TABLE TREES (
    id INT,
    child_id INT
);
INSERT INTO TREES (id, child_id) VALUES
    (1, 2),
    (2, 6),
    (6, 7),
    (7, 8),
    (8, 9),
    (9, 10),
    (11, 10),
    (7, 11),
    (5, 7),
    (2, 5),
    (5, 4),
    (4, 12),
    (12, 5);

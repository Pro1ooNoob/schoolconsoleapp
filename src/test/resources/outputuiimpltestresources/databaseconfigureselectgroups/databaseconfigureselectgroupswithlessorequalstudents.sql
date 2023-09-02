DROP TABLE IF EXISTS school.students CASCADE;
CREATE TABLE school.students (
student_id SERIAL PRIMARY KEY, 
group_id INTEGER REFERENCES school.students (student_id) ON DELETE CASCADE ON UPDATE CASCADE,
first_name TEXT,
last_name TEXT
);

DROP TABLE IF EXISTS school.groups CASCADE;
CREATE TABLE school.groups (
group_id SERIAL PRIMARY KEY,
group_name TEXT
);

INSERT INTO school.groups (group_name) VALUES ('AA-11');
INSERT INTO school.groups (group_name) VALUES ('BB-22');
INSERT INTO school.groups (group_name) VALUES ('CC-33');

INSERT INTO school.students (group_id, first_name, last_name) VALUES (1,'Ethan','Smith');
INSERT INTO school.students (group_id, first_name, last_name) VALUES (2,'Olivia','Johnson');
INSERT INTO school.students (group_id, first_name, last_name) VALUES (2,'Liam', 'Williams');
INSERT INTO school.students (group_id, first_name, last_name) VALUES (3,'Ava','Jones');
INSERT INTO school.students (group_id, first_name, last_name) VALUES (3,'Noah','Brown');
INSERT INTO school.students (group_id, first_name, last_name) VALUES (3,'Emma','Davis');

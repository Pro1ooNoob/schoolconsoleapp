DROP TABLE IF EXISTS school.groups CASCADE;
CREATE TABLE school.groups(
	group_id SERIAL PRIMARY KEY,
	group_name TEXT
);

DROP TABLE IF EXISTS school.students CASCADE;
CREATE TABLE school.students(
	student_id SERIAL PRIMARY KEY,
	group_id INTEGER REFERENCES school.groups (group_id) ON DELETE CASCADE ON UPDATE CASCADE,
	first_name TEXT,
	last_name TEXT
);

DROP TABLE IF EXISTS school.courses CASCADE;
CREATE TABLE school.courses(
	course_id SERIAL PRIMARY KEY,
	course_name TEXT,
	course_description TEXT
);

INSERT INTO school.students (student_id, first_name, last_name) VALUES (1, 'Ben', 'Affleck');

INSERT INTO school.courses (course_name, course_description) VALUES ('Quantum Mechanics and Applications', 'course info');
INSERT INTO school.courses (course_name, course_description) VALUES ('Entrepreneurship and Innovation Strategies', 'course info');
INSERT INTO school.courses (course_name, course_description) VALUES ('Environmental Sustainability and Conservation', 'course info');
INSERT INTO school.courses (course_name, course_description) VALUES ('Artificial Intelligence and Machine Learning', 'course info');

DROP TABLE IF EXISTS school.student_to_course CASCADE;
CREATE TABLE school.student_to_course(
	student_id INTEGER REFERENCES school.students (student_id) ON DELETE CASCADE ON UPDATE CASCADE,
	course_id INTEGER REFERENCES school.courses (course_id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT st_id_co_id_unique UNIQUE(student_id, course_id)
);


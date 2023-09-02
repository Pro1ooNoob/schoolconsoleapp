DROP TABLE IF EXISTS school.courses CASCADE;
CREATE TABLE school.courses (
	course_id SERIAL PRIMARY KEY,
	course_name TEXT UNIQUE NOT NULL, 
	course_description TEXT
	);
INSERT INTO school.courses (course_name, course_description) VALUES ('course1', 'info1');
INSERT INTO school.courses (course_name, course_description) VALUES ('course2', 'info2');
INSERT INTO school.courses (course_name, course_description) VALUES ('course3', 'info3');
INSERT INTO school.courses (course_name, course_description) VALUES ('course4', 'info4');


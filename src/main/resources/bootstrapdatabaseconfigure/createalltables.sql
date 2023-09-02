CREATE TABLE school.courses (
	course_id SERIAL PRIMARY KEY,
	course_name TEXT UNIQUE NOT NULL,
	course_description TEXT DEFAULT('No info')
);
CREATE TABLE school.groups (
	group_id SERIAL PRIMARY KEY,
	group_name TEXT UNIQUE NOT NULL
);
CREATE TABLE school.students (
	student_id SERIAL PRIMARY KEY,
	group_id INTEGER REFERENCES school.groups (group_id) ON DELETE CASCADE ON UPDATE CASCADE,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL
);
CREATE TABLE school.student_to_course ( 
	student_id INTEGER REFERENCES school.students (student_id) ON DELETE CASCADE ON UPDATE CASCADE,
	course_id INTEGER REFERENCES school.courses (course_id) ON DELETE CASCADE ON UPDATE CASCADE, 
	CONSTRAINT unique_constraint UNIQUE (student_id ,course_id)
);
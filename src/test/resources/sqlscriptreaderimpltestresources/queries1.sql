DROP TABLE IF EXISTS school.student_to_course CASCADE;
CREATE TABLE school.student_to_course ( 
	student_id INTEGER REFERENCES school.students (student_id) ON DELETE CASCADE ON UPDATE CASCADE,
	course_id INTEGER REFERENCES school.courses (course_id) ON DELETE CASCADE ON UPDATE CASCADE
);
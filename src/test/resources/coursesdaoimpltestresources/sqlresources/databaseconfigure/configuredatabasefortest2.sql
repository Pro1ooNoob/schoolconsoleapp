INSERT INTO school.students (student_id, first_name, last_name) VALUES (1, 'Ben', 'Affleck');
INSERT INTO school.courses (course_id, course_name, course_description) VALUES (1,'Quantum Mechanics and Applications', 'course info');
INSERT INTO school.courses (course_id, course_name, course_description) VALUES (2,'Entrepreneurship and Innovation Strategies', 'course info');
INSERT INTO school.courses (course_id, course_name, course_description) VALUES (3,'Environmental Sustainability and Conservation', 'course info');
INSERT INTO school.courses (course_id, course_name, course_description) VALUES (4,'Artificial Intelligence and Machine Learning', 'course info');
INSERT INTO school.student_to_course (student_id, course_id) VALUES (1,1);
INSERT INTO school.student_to_course (student_id, course_id) VALUES (1,2);
INSERT INTO school.student_to_course (student_id, course_id) VALUES (1,3);



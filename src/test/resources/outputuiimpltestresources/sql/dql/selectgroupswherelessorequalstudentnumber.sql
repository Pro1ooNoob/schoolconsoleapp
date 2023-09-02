SELECT school.groups.group_id, school.groups.group_name  
FROM school.groups 
WHERE group_id IN (
	SELECT school.groups.group_id 
	FROM school.groups 
	JOIN school.students 
	USING (group_id)  
GROUP BY (school.groups.group_id) 
HAVING COUNT(*) <= ?);
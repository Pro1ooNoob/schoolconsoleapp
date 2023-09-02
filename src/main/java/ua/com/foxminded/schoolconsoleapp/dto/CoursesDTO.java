package ua.com.foxminded.schoolconsoleapp.dto;

import java.util.Objects;

import ua.com.foxminded.schoolconsoleapp.dto.entity.Entity;

public class CoursesDTO extends Entity<Long>{
    
    private String courseName;
    private String courseDescription;
    
    public CoursesDTO(Long courseId, String courseName, String courseDescription) {
        super.id = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }
    
    @Override
    public String toString() {
        return "CoursesDTOImpl [courseId=" + id + ", courseName=" + courseName + ", courseDescription="
                + courseDescription + "]";
    }  
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (this == null || this.getClass() != obj.getClass()) return false;
        
        CoursesDTO other = (CoursesDTO) obj;
        
        return Objects.equals(id, other.id) && 
                Objects.equals(courseName, other.courseName) && 
                Objects.equals(courseDescription, other.courseDescription);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, courseName, courseDescription);
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }
  
}

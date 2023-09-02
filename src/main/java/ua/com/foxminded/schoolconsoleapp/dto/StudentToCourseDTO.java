package ua.com.foxminded.schoolconsoleapp.dto;

import java.util.Objects;
import ua.com.foxminded.schoolconsoleapp.dto.entity.Entity;

public class StudentToCourseDTO extends Entity<Long> {
    private Long courseId;

    public StudentToCourseDTO(Long id, Long courseId) {
        super.id = id;
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return super.id;
    }

    public Long getCourseId() {
        return courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.id, this.courseId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (this == null || this.getClass() != obj.getClass())
            return false;

        StudentToCourseDTO other = (StudentToCourseDTO) obj;

        return Objects.equals(id, other.id) && Objects.equals(courseId, other.courseId);
    }

    @Override
    public String toString() {
        return "StudentToCourseDTO [courseId=" + courseId + " " + "studentId=" + super.id + "]";
    }
    
}

package ua.com.foxminded.schoolconsoleapp.dto;

import java.util.Objects;

import ua.com.foxminded.schoolconsoleapp.dto.entity.Entity;

public class StudentsDTO extends Entity<Long>{
    private Long groupId;
    private String firstName;
    private String lastName;
       
    public StudentsDTO(Long studentId, Long groupId, String firstName, String lastName) {
        super.id = studentId;
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public StudentsDTO(Long groupId, String firstName, String lastName) {
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public StudentsDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentsDTO studentDTO = (StudentsDTO) o;
        return id == studentDTO.id &&
                groupId == studentDTO.groupId &&
                Objects.equals(firstName, studentDTO.firstName) &&
                Objects.equals(lastName, studentDTO.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupId, firstName, lastName);
    }
    
    @Override
    public String toString() {
        return "StudentDTO [studentId=" + id + ", groupId=" + groupId + ", firstName=" + firstName
                + ", lastName=" + lastName + "]";
    }
    
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public Long getGroupId() {
        return groupId;
    }
}

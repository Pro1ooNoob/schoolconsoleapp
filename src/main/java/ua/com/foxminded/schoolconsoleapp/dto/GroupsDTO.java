package ua.com.foxminded.schoolconsoleapp.dto;

import java.util.Objects;

import ua.com.foxminded.schoolconsoleapp.dto.entity.Entity;

public class GroupsDTO extends Entity<Long>{   
    private String groupName;
    
    public GroupsDTO(Long groupId, String groupName) {
        this.groupName = groupName;
        super.id = groupId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(groupName, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GroupsDTO other = (GroupsDTO) obj;
        return Objects.equals(groupName, other.groupName) && id == other.id;       
    }      

    @Override
    public String toString() {
        return "GroupDTOImpl [groupId=" + id + ", groupName=" + groupName + "]";
    }

    public String getGroupName() {
        return groupName;
    }    
}

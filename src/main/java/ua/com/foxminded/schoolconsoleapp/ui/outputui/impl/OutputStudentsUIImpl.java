package ua.com.foxminded.schoolconsoleapp.ui.outputui.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ua.com.foxminded.schoolconsoleapp.dto.StudentsDTO;
import ua.com.foxminded.schoolconsoleapp.ui.outputui.OutputUI;

public class OutputStudentsUIImpl<T extends StudentsDTO> implements OutputUI<T>{
    private final int space = 10;
    private final int space1 = 10;
    private final int space2 = 15;
    private final int space3 = 15;
    private final String firstColumnName = "student_id";
    private final String secondColumnName = "group_id"; 
    private final String thirdColumn = "first_name";
    private final String forthName = "last_name"; 

    @Override
    public String returnView(List<T> listDTO) {
        List<String> resultList = new ArrayList<>();        
        String outputListTitle = 
                String.format("|%-" + space + "s|%-" + space1 + "s|%-" + space2 + "s|%-" + space3 + "s|",
                        firstColumnName, secondColumnName, thirdColumn, forthName);  
        StringBuilder upperLowerFrameStringBuilder = new StringBuilder();
        
        for (int i = 0; i < outputListTitle.length(); i++) {
            upperLowerFrameStringBuilder.append("-");
        }    
        String upperLowerFrame = new String(upperLowerFrameStringBuilder);   
        resultList.add(upperLowerFrame);
        resultList.add(outputListTitle);
        resultList.add(upperLowerFrame);
        
        for (StudentsDTO studentDTO: listDTO) {
            final Long studentId = studentDTO.getId();
            final Long groupId = studentDTO.getGroupId();
            final String firstName = studentDTO.getFirstName();
            final String lastName = studentDTO.getLastName();
            outputListTitle = 
                    String.format("|%-" + space + "d|%-" + space1 + "d|%-" + space2 + "s|%-" + space3 + "s|",
                            studentId, groupId, firstName, lastName);
            resultList.add(outputListTitle);
            resultList.add(upperLowerFrame);
        } 
        String result = resultList.stream() 
                .collect(Collectors.joining("\n"));
        return result;
    }
}

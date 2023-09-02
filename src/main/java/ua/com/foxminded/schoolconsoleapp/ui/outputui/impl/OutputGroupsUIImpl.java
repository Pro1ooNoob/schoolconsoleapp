package ua.com.foxminded.schoolconsoleapp.ui.outputui.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ua.com.foxminded.schoolconsoleapp.dto.GroupsDTO;
import ua.com.foxminded.schoolconsoleapp.ui.outputui.OutputUI;

public class OutputGroupsUIImpl<T extends GroupsDTO> implements OutputUI<T> {
    private final int space = 10;
    private final int space1 = 10;
    private final String firstColumnName = "group_id";
    private final String secondColumnName = "group_name";

    @Override
    public String returnView(List<T> listDTO) {
        List<String> resultList = new ArrayList<>();
        String outputListTitle = String.format("|%-" + space + "s|%-" + space1 + "s|", firstColumnName,
                secondColumnName);
        StringBuilder upperLowerFrameStringBuilder = new StringBuilder();

        for (int i = 0; i < outputListTitle.length(); i++) {
            upperLowerFrameStringBuilder.append("-");
        }
        String upperLowerFrame = new String(upperLowerFrameStringBuilder);
        resultList.add(upperLowerFrame);
        resultList.add(outputListTitle);
        resultList.add(upperLowerFrame);

        for (GroupsDTO groupDTO : listDTO) {
            final Long groupId = groupDTO.getId();
            final String groupName = groupDTO.getGroupName();
            outputListTitle = String.format("|%-" + space + "d|%-" + space1 + "s|", groupId, groupName);
            resultList.add(outputListTitle);
            resultList.add(upperLowerFrame);
        }
        String result = resultList.stream().collect(Collectors.joining("\n"));
        return result;
    }
}

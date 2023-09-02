package ua.com.foxminded.schoolconsoleapp.ui.outputui;

import java.util.List;

public interface OutputUI<T> {
    String returnView(List<T> listDTO);
}

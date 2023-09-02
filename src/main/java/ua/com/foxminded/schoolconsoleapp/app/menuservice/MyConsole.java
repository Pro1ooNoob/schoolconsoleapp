package ua.com.foxminded.schoolconsoleapp.app.menuservice;

import java.io.Closeable;

public interface MyConsole extends Closeable {
	void show() throws Exception;
}

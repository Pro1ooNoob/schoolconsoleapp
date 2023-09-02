package ua.com.foxminded.schoolconsoleapp.app;

import java.io.Closeable;

public abstract class SchoolAppAbstract implements Closeable {
	public abstract void run() throws Exception;
}

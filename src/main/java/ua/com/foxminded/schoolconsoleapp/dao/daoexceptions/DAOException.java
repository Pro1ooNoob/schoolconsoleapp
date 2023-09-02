package ua.com.foxminded.schoolconsoleapp.dao.daoexceptions;

public class DAOException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -4758212267863189079L;

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
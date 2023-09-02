package ua.com.foxminded.schoolconsoleapp.dao.crud.cruddaointerface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.entity.Entity;

public interface CrudDAO <T extends Entity<K> , K>{
    Optional<T> findById(Connection connection, K id) throws SQLException;
    List<T> findAll(Connection connection) throws SQLException;
    T save(Connection connection, T entity) throws SQLException, DAOException;
    T deleteById(Connection connection, K id) throws SQLException, DAOException;
}

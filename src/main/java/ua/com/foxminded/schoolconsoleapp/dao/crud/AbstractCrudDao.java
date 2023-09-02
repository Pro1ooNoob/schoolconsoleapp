package ua.com.foxminded.schoolconsoleapp.dao.crud;

import java.sql.Connection;
import java.sql.SQLException;

import ua.com.foxminded.schoolconsoleapp.dao.crud.cruddaointerface.CrudDAO;
import ua.com.foxminded.schoolconsoleapp.dao.daoexceptions.DAOException;
import ua.com.foxminded.schoolconsoleapp.dto.entity.Entity;

public abstract class AbstractCrudDao <T extends Entity<K>, K> implements CrudDAO<T, K>{
    @Override
    public T save(Connection connection, T entity) throws SQLException, DAOException {
        return entity.getId() == null ? create(connection, entity) : update(connection, entity);
    } 
    protected abstract T create(Connection connection, T entity) throws SQLException, DAOException;
    protected abstract T update(Connection connection, T entity) throws SQLException, DAOException; 
}

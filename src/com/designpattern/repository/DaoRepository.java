package com.designpattern.repository;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Template class for Repository classes
 * @param <T> type of model class
 */
public abstract class DaoRepository<T> {
    private static final String DB_PATH = Paths.get("storage").toAbsolutePath().toString().replaceAll("\\\\", "/");
    private static final String DRIVER_NAME = "com.holub.database.jdbc.JDBCDriver";

    static {
        new File(DB_PATH).mkdirs();
    }

    public DaoRepository() {
        try {
            Class.forName(DRIVER_NAME).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("file:/"+DB_PATH, "harpo", "swordfish");
    }

    public T save(T model) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(getInsertQuery(model));
            return model;
        }
    }

    protected abstract String getInsertQuery(T model);

    public List<T> findAll() throws SQLException {
        return findAllBy(getSelectAllQuery());
    }

    protected List<T> findAllBy(String selectQuery) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            return mapToModelList(resultSet);
        }
    }

    protected abstract String getSelectAllQuery();

    protected List<T> mapToModelList(ResultSet resultSet) {
        List<T> models = new ArrayList<>();
        try {
            while (resultSet.next()) {
                models.add(mapToModel(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return models;
    }

    protected void deleteAllBy(String deleteQuery) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteQuery);
        }
    }

    protected abstract T mapToModel(ResultSet resultSet) throws SQLException;

    protected void createTableWith(String createQuery) {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

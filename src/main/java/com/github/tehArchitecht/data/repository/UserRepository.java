package com.github.tehArchitecht.data.repository;

import com.github.tehArchitecht.data.ConnectionFactory;
import com.github.tehArchitecht.data.DbAccessException;
import com.github.tehArchitecht.data.DbUtils;
import com.github.tehArchitecht.data.model.User;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private final static Logger logger = Logger.getLogger(UserRepository.class);

    public static boolean save(User user) throws DbAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        int updateStatus = 0;

        try {
            String statementString = "INSERT INTO User VALUES (default, ?, ?, ?, ?)";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);

            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAddress());
            statement.setString(4, user.getPhoneNumber());

            updateStatus = statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Couldn't insert user " + user.toString());
            logger.error(e);
            throw new DbAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return updateStatus != 0;
    }

    public static boolean existsByName(String name) throws DbAccessException {
        return findByName(name) != null;
    }

    public static boolean existsByPhoneNumber(String phoneNumber) throws DbAccessException {
        return findByPhoneNumber(phoneNumber) != null;
    }

    public static User findByName(String name) throws DbAccessException {
        return findByNameOrPhoneNumber(name, null);
    }

    public static User findByPhoneNumber(String phoneNumber) throws DbAccessException {
        return findByNameOrPhoneNumber(null, phoneNumber);
    }

    private static User findByNameOrPhoneNumber(String name, String phoneNumber) throws DbAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            connection = ConnectionFactory.getConnection();
            if (name == null) {
                String statementString = "SELECT * FROM User WHERE phone_number=?";
                statement = connection.prepareStatement(statementString);
                statement.setString(1, phoneNumber);
            } else {
                String statementString = "SELECT * FROM User WHERE name=?";
                statement = connection.prepareStatement(statementString);
                statement.setString(1, name);
            }

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = getFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            if (name == null) {
                logger.error("Couldn't select user by phone number: " + phoneNumber);
            } else {
                logger.error("Couldn't select user by name: " + name);
            }
            logger.error(e);
            throw new DbAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return user;
    }

    private static User getFromResultSet(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("password"),
                resultSet.getString("address"),
                resultSet.getString("phone_number")
        );
    }
}

package com.github.tehArchitecht.jdbcbankingapp.data.repository;

import com.github.tehArchitecht.jdbcbankingapp.data.ConnectionFactory;
import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.DbUtils;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {
    private final static Logger logger = Logger.getLogger(UserRepository.class);

    public static void save(User user) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "INSERT INTO User VALUES (default, ?, ?, ?, ?)";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);

            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAddress());
            statement.setString(4, user.getPhoneNumber());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Couldn't insert user " + user.toString());
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    public static Optional<User> findByName(String name) throws DataAccessException {
        return findByNameOrPhoneNumber(name, null);
    }

    public static Optional<User> findByPhoneNumber(String phoneNumber) throws DataAccessException {
        return findByNameOrPhoneNumber(null, phoneNumber);
    }

    public static boolean existsByName(String name) throws DataAccessException {
        return findByName(name).isPresent();
    }

    public static boolean existsByPhoneNumber(String phoneNumber) throws DataAccessException {
        return findByPhoneNumber(phoneNumber).isPresent();
    }

    private static Optional<User> findByNameOrPhoneNumber(String name, String phoneNumber) throws DataAccessException {
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
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return Optional.ofNullable(user);
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

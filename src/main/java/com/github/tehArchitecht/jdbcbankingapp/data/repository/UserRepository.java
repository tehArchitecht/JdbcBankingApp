package com.github.tehArchitecht.jdbcbankingapp.data.repository;

import com.github.tehArchitecht.jdbcbankingapp.data.ConnectionFactory;
import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.DbUtils;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {
    private final static Logger logger = Logger.getLogger(UserRepository.class);

    public static User save(User user) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "INSERT INTO User VALUES (default, ?, ?, ?, ?, ?)";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAddress());
            statement.setString(4, user.getPhoneNumber());
            statement.setObject(5, user.getPrimaryAccountId());

            int numAffectedRows = statement.executeUpdate();
            if (numAffectedRows == 0) {
                logger.error("Couldn't insert user " + user.toString() + ", no rows affected");
                throw new DataAccessException();
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                updateWithResultSet(user, generatedKeys);
            } else {
                logger.error("Couldn't insert user " + user.toString() + ", no generated keys obtained");
                throw new DataAccessException();
            }
        } catch (SQLException e) {
            logger.error("Couldn't insert user " + user.toString());
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return user;
    }

    public static Optional<User> findById(Long id) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            String statementString = "SELECT * FROM User WHERE id=?";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);
            statement.setLong(1, id);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = getFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Couldn't select user by id: " + id);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return Optional.ofNullable(user);
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

    public static void setPrimaryAccountIdById(Long id, UUID primaryAccountId) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "UPDATE User SET primary_account_id=? WHERE id=?;";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);

            statement.setObject(1, primaryAccountId);
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Couldn't update primary_account_id for user id " + id);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    private static User getFromResultSet(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("password"),
                resultSet.getString("address"),
                resultSet.getString("phone_number"),
                (UUID) resultSet.getObject("primary_account_id")
        );
    }

    private static void updateWithResultSet(User user, ResultSet resultSet) throws SQLException {
        user.setId(resultSet.getLong("id"));
    }
}

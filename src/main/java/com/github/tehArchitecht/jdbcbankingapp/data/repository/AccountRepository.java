package com.github.tehArchitecht.jdbcbankingapp.data.repository;

import com.github.tehArchitecht.jdbcbankingapp.data.ConnectionFactory;
import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.DbUtils;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.sql.SQLException;

public class AccountRepository {
    private final static Logger logger = Logger.getLogger(AccountRepository.class);

    public static void save(Account account) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "INSERT INTO Account VALUES (default, default, ?, ?, ?)";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);

            statement.setLong(1, account.getUserId());
            statement.setBigDecimal(2, account.getBalance());
            statement.setString(3, account.getCurrency().toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Couldn't insert account " + account.toString());
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    public static Optional<Account> findById(UUID accountId) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Account account = null;

        try {
            String statementString = "SELECT * FROM Account WHERE id=?";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);
            statement.setObject(1, accountId);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                account = getFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Couldn't select account by id: " + accountId);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return Optional.ofNullable(account);
    }

    public static void setAccountBalanceById(UUID accountId, BigDecimal balance) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "UPDATE Account SET balance=? WHERE id=?;";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);

            statement.setBigDecimal(1, balance);
            statement.setObject(2, accountId);

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Couldn't update balance for account id " + accountId);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    public static List<Account> findAllByUserId(Long userId) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Account> accounts = new ArrayList<>();

        try {
            String statementString = "SELECT * FROM Account WHERE user_id=? ORDER BY id";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);
            statement.setLong(1, userId);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                accounts.add(getFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Couldn't select accounts by user id " + userId);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return accounts;
    }

    public static int countByUserId(Long userId) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;

        try {
            String statementString = "SELECT COUNT(*) FROM Account WHERE user_id=?";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);
            statement.setObject(1, userId);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Couldn't count accounts by user id: " + userId);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return count;
    }

    private static Account getFromResultSet(ResultSet resultSet) throws SQLException {
        return new Account(
                (UUID)resultSet.getObject("id"),
                resultSet.getLong("number"),
                resultSet.getLong("user_id"),
                resultSet.getBigDecimal("balance"),
                Currency.valueOf(resultSet.getString("currency"))
        );
    }
}

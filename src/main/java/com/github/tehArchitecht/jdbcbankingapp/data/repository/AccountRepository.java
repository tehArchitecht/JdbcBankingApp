package com.github.tehArchitecht.jdbcbankingapp.data.repository;

import com.github.tehArchitecht.jdbcbankingapp.data.ConnectionFactory;
import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.DbUtils;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountRepository {
    private final static Logger logger = Logger.getLogger(AccountRepository.class);

    public static Account save(Account account) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "INSERT INTO Account VALUES (default, ?, ?, ?)";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString, Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, account.getUserId());
            statement.setBigDecimal(2, account.getBalance());
            statement.setString(3, account.getCurrency().toString());

            int numAffectedRows = statement.executeUpdate();
            if (numAffectedRows == 0) {
                logger.error("Couldn't insert account " + account.toString() + ", no rows affected");
                throw new DataAccessException();
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                updateWithResultSet(account, generatedKeys);
            } else {
                logger.error("Couldn't insert account " + account.toString() + ", no generated keys obtained");
                throw new DataAccessException();
            }
        } catch (SQLException e) {
            logger.error("Couldn't insert account " + account.toString());
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return account;
    }

    public static Optional<Account> findById(UUID id) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Account account = null;

        try {
            String statementString = "SELECT * FROM Account WHERE id=?";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);
            statement.setObject(1, id);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                account = getFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Couldn't select account by id: " + id);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return Optional.ofNullable(account);
    }

    public static void setAccountBalanceById(UUID id, BigDecimal balance) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "UPDATE Account SET balance=? WHERE id=?;";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);

            statement.setBigDecimal(1, balance);
            statement.setObject(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Couldn't update balance for account id " + id);
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

    private static Account getFromResultSet(ResultSet resultSet) throws SQLException {
        return new Account(
                (UUID)resultSet.getObject("id"),
                resultSet.getLong("user_id"),
                resultSet.getBigDecimal("balance"),
                Currency.valueOf(resultSet.getString("currency"))
        );
    }

    private static void updateWithResultSet(Account account, ResultSet resultSet) throws SQLException {
        account.setId((UUID) resultSet.getObject("id"));
    }
}

package com.github.tehArchitecht.data.repository;

import com.github.tehArchitecht.data.ConnectionFactory;
import com.github.tehArchitecht.data.exception.DataAccessException;
import com.github.tehArchitecht.data.DbUtils;
import com.github.tehArchitecht.data.model.Operation;
import com.github.tehArchitecht.data.model.Currency;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OperationRepository {
    private final static Logger logger = Logger.getLogger(OperationRepository.class);

    public static void save(Operation operation) throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String statementString = "INSERT INTO Operation VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);

            statement.setTimestamp(1, operation.getDate());
            statement.setString(2, operation.getCurrency().toString());
            statement.setObject(3, operation.getSenderAccountId());
            statement.setObject(4, operation.getReceiverAccountId());
            statement.setBigDecimal(5, operation.getAmount());
            statement.setBigDecimal(6, operation.getSenderInitialBalance());
            statement.setBigDecimal(7, operation.getSenderResultingBalance());
            statement.setBigDecimal(8, operation.getReceiverInitialBalance());
            statement.setBigDecimal(9, operation.getReceiverResultingBalance());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Couldn't insert operation " + operation.toString());
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }

    public static List<Operation> findDistinctBySenderAccountIdOrReceiverAccountId(UUID accountId)
            throws DataAccessException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Operation> operations = new ArrayList<>();

        try {
            String statementString = "SELECT DISTINCT * FROM Operation WHERE sender_account_id=? OR receiver_account_id=?";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(statementString);
            statement.setObject(1, accountId);
            statement.setObject(2, accountId);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                operations.add(getFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Couldn't select operations by account id " + accountId);
            logger.error(e);
            throw new DataAccessException();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }

        return operations;
    }

    private static Operation getFromResultSet(ResultSet resultSet) throws SQLException {
        return new Operation(
                resultSet.getLong("id"),
                resultSet.getTimestamp("date"),
                Currency.valueOf(resultSet.getString("currency")),
                (UUID)resultSet.getObject("sender_account_id"),
                (UUID)resultSet.getObject("receiver_account_id"),
                resultSet.getBigDecimal("amount"),
                resultSet.getBigDecimal("sender_initial_balance"),
                resultSet.getBigDecimal("sender_resulting_balance"),
                resultSet.getBigDecimal("receiver_initial_balance"),
                resultSet.getBigDecimal("receiver_resulting_balance")
        );
    }
}

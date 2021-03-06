package com.github.tehArchitecht.jdbcbankingapp.data.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Provides methods to silently close JDBC objects. Logs caught exceptions
 * using log4j.
 */
public class DbUtils {
    private final static Logger logger = Logger.getLogger(DbUtils.class);

    public static void closeQuietly(Connection connection) {
        try { connection.close(); } catch (Exception e) {
            logger.error(e);
        }
    }
    public static void closeQuietly(Statement statement) {
        try { statement.close(); } catch (Exception e) {
            logger.error(e);
        }
    }
    public static void closeQuietly(PreparedStatement statement) {
        try { statement.close(); } catch (Exception e) {
            logger.error(e);
        }
    }
    public static void closeQuietly(ResultSet resultSet) {
        try { resultSet.close(); } catch (Exception e) {
            logger.error(e);
        }
    }
}

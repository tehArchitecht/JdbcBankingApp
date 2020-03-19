package com.github.tehArchitecht.data;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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

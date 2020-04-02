package com.github.tehArchitecht.jdbcbankingapp.data;

import com.github.tehArchitecht.jdbcbankingapp.util.ResourceLoader;
import org.apache.log4j.Logger;
import org.h2.util.ScriptReader;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the database from an SQL script located by the SCRIPT_PATH
 * constant.
 */
public class DbInitializer {
    private static final Logger logger = Logger.getLogger(DbInitializer.class);

    private static final String SCRIPT_PATH = "create-schema.sql";

    public static void initialize() {
        InputStreamReader scriptStreamReader = new InputStreamReader(ResourceLoader.getResource(SCRIPT_PATH));
        ScriptReader scriptReader = new ScriptReader(scriptStreamReader);

        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.createStatement();

            while (true) {
                String fragment = scriptReader.readStatement();
                if (fragment == null) break;
                statement.execute(fragment);
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
        }
    }
}

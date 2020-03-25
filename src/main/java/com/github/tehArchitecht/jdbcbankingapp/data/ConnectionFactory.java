package com.github.tehArchitecht.jdbcbankingapp.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.github.tehArchitecht.jdbcbankingapp.util.PropertyLoader;
import org.h2.jdbcx.JdbcConnectionPool;

/**
 * Used to connect to the database (by url from the property file at
 * PROPERTY_FILE_PATH). Provides a single static method getConnection to
 * retrieve an SQLException object.
 */
public class ConnectionFactory {
    private static final String PROPERTY_FILE_PATH = "db.properties";

    private static final String URL_PROPERTY_KEY = "url";
    private static final String USER_PROPERTY_KEY = "user";
    private static final String PASSWORD_PROPERTY_KEY = "password";

    private static JdbcConnectionPool connectionPool = null;

    public static Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            try {
                PropertyLoader propertyLoader = new PropertyLoader(PROPERTY_FILE_PATH);
                connectionPool = JdbcConnectionPool.create(
                        propertyLoader.getProperty(URL_PROPERTY_KEY),
                        propertyLoader.getProperty(USER_PROPERTY_KEY),
                        propertyLoader.getProperty(PASSWORD_PROPERTY_KEY)
                );
            } catch (IOException e) {
                // Not going to provide a way to recover from this.
                throw new Error("Couldn't load DB access information");
            }
        }

        return connectionPool.getConnection();
    }
}

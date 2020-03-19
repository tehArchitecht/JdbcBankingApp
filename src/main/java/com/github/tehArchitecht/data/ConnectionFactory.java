package com.github.tehArchitecht.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.github.tehArchitecht.util.PropertyLoader;
import org.h2.jdbcx.JdbcConnectionPool;

public class ConnectionFactory {
    private static final String URL_PROPERTY_KEY = "url";
    private static final String USER_PROPERTY_KEY = "user";
    private static final String PASSWORD_PROPERTY_KEY = "password";

    private static JdbcConnectionPool connectionPool = null;

    public static Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            try {
                PropertyLoader propertyLoader = new PropertyLoader("db.properties");
                connectionPool = JdbcConnectionPool.create(
                        propertyLoader.getPropery(URL_PROPERTY_KEY),
                        propertyLoader.getPropery(USER_PROPERTY_KEY),
                        propertyLoader.getPropery(PASSWORD_PROPERTY_KEY)
                );
            } catch (IOException e) {
                // Not going to provide a way to recover from this.
                throw new Error("Couldn't load DB access information");
            }
        }

        return connectionPool.getConnection();
    }
}

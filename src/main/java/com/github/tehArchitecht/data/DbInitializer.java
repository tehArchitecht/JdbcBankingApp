package com.github.tehArchitecht.data;

import com.github.tehArchitecht.util.ResourceLoader;
import org.apache.log4j.Logger;
import org.h2.util.ScriptReader;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbInitializer {
    private static final Logger logger = Logger.getLogger(DbInitializer.class);

    private static final String SCRIPT_PATH = "create-schema.sql";

    public static void Initize() {
        InputStreamReader scriptStreamReader = new InputStreamReader(ResourceLoader.getRsource(SCRIPT_PATH));
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

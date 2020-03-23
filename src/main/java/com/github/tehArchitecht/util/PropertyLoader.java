package com.github.tehArchitecht.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Loads property files by path relative to the resources directory. Provides
 * a getProperty method to read property values.
 */
public class PropertyLoader {
    private final Properties properties;

    public PropertyLoader(String path) throws IOException {
        properties = new Properties();
        properties.load(ResourceLoader.getResource(path));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}

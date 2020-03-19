package com.github.tehArchitecht.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {
    private Properties properties;

    public PropertyLoader(String path) throws IOException {
        properties = new Properties();
        properties.load(ResourceLoader.getRsource(path));
    }

    public String getPropery(String key) {
        return properties.getProperty(key);
    }
}

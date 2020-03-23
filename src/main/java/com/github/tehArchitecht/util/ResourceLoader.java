package com.github.tehArchitecht.util;

import java.io.InputStream;

/**
 * Provides a single static method getResource to read files (as InputStream
 * objects) by their paths relative to the resources directory.
 */
public class ResourceLoader {
    public static InputStream getResource(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}

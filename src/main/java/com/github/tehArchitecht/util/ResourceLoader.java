package com.github.tehArchitecht.util;

import java.io.InputStream;

public class ResourceLoader {
    public static InputStream getRsource(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}

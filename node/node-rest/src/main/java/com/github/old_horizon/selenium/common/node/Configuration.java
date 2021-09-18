package com.github.old_horizon.selenium.common.node;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Configuration {
    private static final Configuration INSTANCE = new Configuration();

    private Configuration() {
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    public Path getDownloadDirectory() {
        return Optional.ofNullable(getSystemProperty("download.directory"))
                .map(Paths::get)
                .orElseGet(() -> Paths.get(getSystemProperty("user.home")).resolve("Downloads"));
    }

    String getSystemProperty(String key) {
        return System.getProperty(key);
    }
}

package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();
    static {
        try (FileInputStream file =
                       new FileInputStream("src/test/resources/config.properties")) {
            props.load(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties", e);
        }
    }
    public static String get(String key) {
        String envKey = key.toUpperCase().replace(".", "_");   // api.key → API_KEY
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return props.getProperty(key);
    }
    public static String getRequired(String key) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required config: " + key + " (set env var "
                            + key.toUpperCase().replace(".", "_") + " or add it to config.properties)");
        }
        return value;
    }
}

package org.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties;

    public static String getProperty(String key) {
        if (properties == null) {
            properties = new Properties();
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input == null) {
                    throw new IOException("Unable to find application.properties");
                }
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load properties file");
            }
        }
        return properties.getProperty(key);
    }

//    String apiKey = ConfigLoader.getProperty("api.key");

}


package com.testing.framework.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for handling environment-specific properties
 */
public class ConfigManager {
    
    private static ConfigManager instance;
    private Properties properties;
    private String environment;
    
    private ConfigManager() {
        this.environment = System.getProperty("env", "dev");
        loadProperties();
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        String configFile = String.format("config/%s.properties", environment);
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new RuntimeException("Unable to find config file: " + configFile);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    // API Configuration
    public String getApiBaseUrl() {
        return getProperty("api.base.url");
    }
    
    public int getApiTimeout() {
        return Integer.parseInt(getProperty("api.timeout", "30000"));
    }
    
    // UI Configuration
    public String getUiBaseUrl() {
        return getProperty("ui.base.url");
    }
    
    public String getBrowser() {
        return getProperty("browser", "chromium");
    }
    
    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "true"));
    }
    
    // Database Configuration (if needed)
    public String getDatabaseUrl() {
        return getProperty("db.url");
    }
    
    public String getDatabaseUser() {
        return getProperty("db.user");
    }
    
    public String getDatabasePassword() {
        return getProperty("db.password");
    }
}

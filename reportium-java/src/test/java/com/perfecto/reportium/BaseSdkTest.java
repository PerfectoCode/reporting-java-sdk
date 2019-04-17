package com.perfecto.reportium;

import org.testng.annotations.AfterMethod;

import java.util.HashMap;
import java.util.Map;

public class BaseSdkTest {
    private Map<String, String> systemProperties = new HashMap<>();

    @AfterMethod
    public void teardown() {
        restoreSystemProperties();
    }

    protected void backupSystemProperties(String... properties) {
        systemProperties = new HashMap<>();
        for (String property : properties) {
            systemProperties.put(property, System.getProperty(property));
        }
    }

    protected void restoreSystemProperties() {
        for (Map.Entry<String, String> entry : systemProperties.entrySet()) {
            restoreParamValue(entry.getKey(), entry.getValue());
        }
    }

    private void restoreParamValue(String key, String previousValue) {
        // Restore system properties
        if (previousValue != null) {
            // Throws an NPE if existingTags is null
            System.setProperty(key, previousValue);
        } else {
            // If this prop didn't have a value before we need to make sure it is nullified now
            System.clearProperty(key);
        }
    }
}

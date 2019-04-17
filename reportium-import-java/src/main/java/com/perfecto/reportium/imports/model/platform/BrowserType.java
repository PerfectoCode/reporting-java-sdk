package com.perfecto.reportium.imports.model.platform;

import java.util.Objects;

public enum BrowserType {
    CHROME,
    FIREFOX,
    INTERNET_EXPLORER,
    SAFARI,
    MICROSOFT_EDGE;

    public static BrowserType getByName(String type) {
        if (type != null) {
            type = type.toUpperCase().replace(" ", "_");
            for (BrowserType browserType : BrowserType.values()) {
                if (Objects.equals(type.toUpperCase(), browserType.toString().toUpperCase())) {
                    return browserType;
                }
            }
        }
        return null;
    }
}

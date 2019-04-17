package com.perfecto.reportium.imports.model.platform;

import java.util.Objects;

/**
 * Created by michaeld on 23/02/2016.
 */
public class BrowserInfo {

    private final BrowserType browserType;
    private final String browserVersion;

    public BrowserInfo(BrowserType browserType, String browserVersion) {
        this.browserType = browserType;
        this.browserVersion = browserVersion;
    }

    private BrowserInfo(Builder builder) {
        this.browserType = builder.browserType;
        this.browserVersion = builder.browserVersion;
    }

    public BrowserType getBrowserType() {
        return browserType;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowserInfo that = (BrowserInfo) o;
        return browserType == that.browserType &&
                Objects.equals(browserVersion, that.browserVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(browserType, browserVersion);
    }

    public static final class Builder {
        private BrowserType browserType;
        private String browserVersion;

        public Builder() {
        }

        public Builder(BrowserInfo copy) {
            this.browserType = copy.browserType;
            this.browserVersion = copy.browserVersion;
        }

        public Builder withBrowserType(BrowserType browserType) {
            this.browserType = browserType;
            return this;
        }

        public Builder withBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
            return this;
        }

        public BrowserInfo build() {
            return new BrowserInfo(this);
        }
    }
}

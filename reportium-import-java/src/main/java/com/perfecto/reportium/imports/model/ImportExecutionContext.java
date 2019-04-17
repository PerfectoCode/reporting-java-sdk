package com.perfecto.reportium.imports.model;

import com.perfecto.reportium.imports.model.platform.*;
import com.perfecto.reportium.model.BaseExecutionContext;
import com.perfecto.reportium.model.CustomField;

import java.util.*;

/**
 * Execution context for generic ReportiumClient clients
 */
public class ImportExecutionContext extends BaseExecutionContext {

    public static final String APPIUM_PLATFORM_NAME = "platformName";
    public static final String APPIUM_PLATFORM_VERSION = "platformVersion";
    public static final String APPIUM_DEVICE_NAME = "deviceName";

    public static final String SELENIUM_BROWSER_NAME = "browserName";
    public static final String SELENIUM_PLATFORM = "platform";
    public static final String SELENIUM_VERSION = "version";

    private final String externalId;
    private List<Platform> platforms = new ArrayList<>();
    private String automationFramework;

    private ImportExecutionContext(Builder builder) {
        super(builder);
        this.externalId = builder.externalId;
        this.platforms = builder.platforms;
        this.automationFramework = builder.automationFramework;
    }

    public String getExternalId() {
        return externalId;
    }

    public List<Platform> getPlatforms() {
        return Collections.unmodifiableList(platforms);
    }

    public String getAutomationFramework() {
        return automationFramework;
    }

    public void updatePlatforms(Map<String, ?> capabilities) {
        if (!platforms.isEmpty() || capabilities == null || capabilities.isEmpty()) {
            return;
        }
        Platform.Builder platformBuilder = new Platform.Builder();

        if (capabilities.containsKey(SELENIUM_VERSION)) {
            BrowserInfo.Builder browserInfoBuilder = new BrowserInfo.Builder();

            BrowserType browserType = BrowserType.getByName(capabilities.get(SELENIUM_BROWSER_NAME).toString());
            if (browserType != null) {
                browserInfoBuilder.withBrowserType(browserType);
            }

            if (capabilities.containsKey(SELENIUM_VERSION)) {
                browserInfoBuilder.withBrowserVersion(capabilities.get(SELENIUM_VERSION).toString());
            }

            if (capabilities.containsKey(SELENIUM_PLATFORM)) {
                platformBuilder.withOs(capabilities.get(SELENIUM_PLATFORM).toString());
            }

            platformBuilder.withBrowserInfo(browserInfoBuilder.build());
            platformBuilder.withDeviceType(DeviceType.DESKTOP);

        } else if (capabilities.containsKey(APPIUM_PLATFORM_VERSION)) {
            MobileInfo.Builder mobileInfoBuilder = new MobileInfo.Builder();

            platformBuilder.withOs(capabilities.get(APPIUM_PLATFORM_NAME).toString());

            if (capabilities.containsKey(APPIUM_PLATFORM_VERSION)) {
                platformBuilder.withOsVersion(capabilities.get(APPIUM_PLATFORM_VERSION).toString());
            }

            if (capabilities.containsKey(APPIUM_DEVICE_NAME)) {
                mobileInfoBuilder.withModel(capabilities.get(APPIUM_DEVICE_NAME).toString());
            }

            platformBuilder.withMobileInfo(mobileInfoBuilder.build());
            platformBuilder.withDeviceType(DeviceType.MOBILE);
        }
        platforms = Collections.singletonList(platformBuilder.build());
    }

    public static class Builder extends BaseExecutionContext.Builder<Builder> {
        private List<Platform> platforms = new ArrayList<>();
        private String externalId = null;
        private String automationFramework = null;

        public Builder withExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder withPlatforms(Platform... platforms) {
            if (platforms != null) {
                withPlatforms(Arrays.asList(platforms));
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder withPlatforms(Collection<Platform> platforms) {
            if (platforms != null && platforms.size() > 0) {
                for (Platform platform : platforms) {
                    if (platform != null) {
                        this.platforms.add(platform);
                    }
                }
            }
            return this;
        }

        /**
         * Sets the automation framework that was used to execute the test. For example: Selenium, UFT, XCTest, etc.
         * @param automationFramework
         * @return
         */
        public Builder withAutomationFramework(String automationFramework) {
            this.automationFramework = automationFramework;
            return this;
        }

        public ImportExecutionContext build() {
            return new ImportExecutionContext(this);
        }
    }
}

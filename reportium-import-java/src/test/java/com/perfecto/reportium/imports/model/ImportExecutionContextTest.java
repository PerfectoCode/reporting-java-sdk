package com.perfecto.reportium.imports.model;


import com.perfecto.reportium.imports.model.platform.*;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.perfecto.reportium.imports.model.ImportExecutionContext.*;
import static org.testng.Assert.*;

public class ImportExecutionContextTest {

    @Test
    public void updatePlatforms_nullCapabilities() {
        ImportExecutionContext context = new ImportExecutionContext.Builder().build();
        assertEquals(0, context.getPlatforms().size());

        context.updatePlatforms(null);
        assertEquals(0, context.getPlatforms().size());
    }

    @Test
    public void updatePlatforms_emptyCapabilities() {
        ImportExecutionContext context = new ImportExecutionContext.Builder().build();
        assertEquals(0, context.getPlatforms().size());

        context.updatePlatforms(new HashMap<String, String>());
        assertEquals(0, context.getPlatforms().size());
    }

    @Test
    public void updatePlatforms_seleniumCapabilities() {
        ImportExecutionContext context = new ImportExecutionContext.Builder().build();
        assertEquals(0, context.getPlatforms().size());

        final String browserName = "Firefox";
        final String platformName = "XP";
        final String version = "30";

        Map<String, String> capabilities = new HashMap<>();
        capabilities.put(SELENIUM_BROWSER_NAME, browserName);
        capabilities.put(SELENIUM_PLATFORM, platformName);
        capabilities.put(SELENIUM_VERSION, version);
        context.updatePlatforms(capabilities);

        assertEquals(1, context.getPlatforms().size());
        Platform platform = context.getPlatforms().iterator().next();
        assertEquals(DeviceType.DESKTOP, platform.getDeviceType());
        assertEquals(platformName, platform.getOs());

        BrowserInfo browserInfo = platform.getBrowserInfo();
        assertNotNull(browserInfo);
        assertEquals(BrowserType.FIREFOX, browserInfo.getBrowserType());
        assertEquals(version, browserInfo.getBrowserVersion());

        assertNull(platform.getMobileInfo());
    }

    @Test
    public void updatePlatforms_appiumCapabilities() {
        ImportExecutionContext context = new ImportExecutionContext.Builder().build();
        assertEquals(0, context.getPlatforms().size());

        final String platformName = "iOS";
        final String platformVersion = "11.0";
        final String deviceName = "iPhone 7";

        Map<String, String> capabilities = new HashMap<>();
        capabilities.put(APPIUM_PLATFORM_NAME, platformName);
        capabilities.put(APPIUM_PLATFORM_VERSION, platformVersion);
        capabilities.put(APPIUM_DEVICE_NAME, deviceName);
        context.updatePlatforms(capabilities);

        assertEquals(1, context.getPlatforms().size());
        Platform platform = context.getPlatforms().iterator().next();
        assertEquals(DeviceType.MOBILE, platform.getDeviceType());
        assertEquals(platformName, platform.getOs());
        assertEquals(platformVersion, platform.getOsVersion());

        MobileInfo mobileInfo = platform.getMobileInfo();
        assertNotNull(mobileInfo);
        assertEquals(deviceName, mobileInfo.getModel());

        assertNull(platform.getBrowserInfo());
    }

    @Test
    public void updatePlatforms_appiumCapabilities_platformAlreadyExists() {
        ImportExecutionContext context = new ImportExecutionContext.Builder()
                .withPlatforms(new Platform.Builder()
                        .withDeviceType(DeviceType.DESKTOP)
                        .withBrowserInfo(new BrowserInfo.Builder()
                                .withBrowserType(BrowserType.CHROME)
                                .withBrowserVersion("65")
                                .build())
                        .build())
                .build();

        final String platformName = "iOS";
        final String platformVersion = "11.0";
        final String deviceName = "iPhone 7";

        Map<String, String> capabilities = new HashMap<>();
        capabilities.put(APPIUM_PLATFORM_NAME, platformName);
        capabilities.put(APPIUM_PLATFORM_VERSION, platformVersion);
        capabilities.put(APPIUM_DEVICE_NAME, deviceName);
        context.updatePlatforms(capabilities);

        assertEquals(1, context.getPlatforms().size());
        Platform platform = context.getPlatforms().iterator().next();
        assertEquals(DeviceType.DESKTOP, platform.getDeviceType());
        assertNull(platform.getOs());
        assertNull(platform.getOsVersion());

        BrowserInfo browserInfo = platform.getBrowserInfo();
        assertNotNull(browserInfo);
        assertEquals(BrowserType.CHROME, browserInfo.getBrowserType());
        assertEquals("65", browserInfo.getBrowserVersion());

        assertNull(platform.getMobileInfo());
    }

    @Test
    public void setNullPlatform() {
        Platform platform = null;
        ImportExecutionContext context = new ImportExecutionContext.Builder()
                .withPlatforms(platform)
                .build();

        assertEquals(0, context.getPlatforms().size());
    }
}
